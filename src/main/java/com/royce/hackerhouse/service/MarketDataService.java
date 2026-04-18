package com.royce.hackerhouse.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.royce.hackerhouse.dto.MarketDtos.CommentResponse;
import com.royce.hackerhouse.dto.MarketDtos.EventResponse;
import com.royce.hackerhouse.dto.MarketDtos.MarketStatResponse;
import com.royce.hackerhouse.dto.MarketDtos.PricePointResponse;
import com.royce.hackerhouse.dto.MarketDtos.TicketResponse;
import com.royce.hackerhouse.dto.PriceTickPayload;
import com.royce.hackerhouse.entity.CommentEntity;
import com.royce.hackerhouse.entity.EventEntity;
import com.royce.hackerhouse.entity.TicketEntity;
import com.royce.hackerhouse.repository.CommentRepository;
import com.royce.hackerhouse.repository.EventRepository;
import com.royce.hackerhouse.repository.TicketRepository;
import com.royce.hackerhouse.websocket.PriceWebSocketHandler;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketDataService {

    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;
    private final ObjectMapper objectMapper;
    private final PriceWebSocketHandler priceWebSocketHandler;

    private final Map<String, Double> realtimePriceByKey = new ConcurrentHashMap<>();

    public List<EventResponse> getEvents() {
        return getEvents(0, 6);
    }

    public List<EventResponse> getEvents(int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = size <= 0 ? 6 : Math.min(size, 50);
        return eventRepository.findAll(PageRequest.of(safePage, safeSize)).stream().map(this::toEventResponse).toList();
    }

    public Optional<EventResponse> getEventById(String eventId) {
        return eventRepository.findById(parseUuid(eventId)).map(this::toEventResponse);
    }

    public List<TicketResponse> getTicketsByEvent(String eventId) {
        return ticketRepository.findAllByEventId(parseUuid(eventId)).stream()
            .map(ticket -> TicketResponse.builder()
                .id(ticket.getSuiObjectId() == null || ticket.getSuiObjectId().isBlank()
                    ? ticket.getId().toString()
                    : ticket.getSuiObjectId())
                .eventId(ticket.getEventId().toString())
                .ticketClass(extractTicketClass(ticket.getMetadata()))
                .priceSui(ticket.getPriceSui() == null ? 0.0 : ticket.getPriceSui().doubleValue())
                .status(ticket.getStatus() == null ? "available" : ticket.getStatus().name())
                .build())
            .toList();
    }

    public List<CommentResponse> getCommentsByEvent(String eventId) {
        List<CommentEntity> rows = commentRepository.findAllByEventIdOrderByTimestampDesc(eventId);
        if (rows.isEmpty()) {
            return List.of();
        }

        Map<String, CommentResponse> byId = new LinkedHashMap<>();
        for (CommentEntity row : rows) {
            byId.put(row.getId(), CommentResponse.builder()
                .id(row.getId())
                .author(row.getAuthor())
                .avatar(row.getAvatar())
                .content(row.getContent())
                .timestamp(row.getTimestamp())
                .likes(row.getLikes())
                .replies(new ArrayList<>())
                .build());
        }

        List<CommentResponse> roots = new ArrayList<>();
        for (CommentEntity row : rows) {
            CommentResponse current = byId.get(row.getId());
            if (row.getParentId() == null || row.getParentId().isBlank()) {
                roots.add(current);
            } else {
                CommentResponse parent = byId.get(row.getParentId());
                if (parent != null) {
                    parent.getReplies().add(current);
                } else {
                    roots.add(current);
                }
            }
        }

        return roots;
    }

    public CommentResponse addComment(String eventId, CommentResponse comment) {
        String id = (comment.getId() == null || comment.getId().isBlank())
            ? "c_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8)
            : comment.getId();

        CommentEntity row = CommentEntity.builder()
            .id(id)
            .eventId(eventId)
            .parentId(null)
            .author(comment.getAuthor() == null ? "Anonymous" : comment.getAuthor())
            .avatar(comment.getAvatar() == null ? "https://api.dicebear.com/7.x/avataaars/svg?seed=anon" : comment.getAvatar())
            .content(comment.getContent() == null ? "" : comment.getContent())
            .timestamp(comment.getTimestamp() == null ? "just now" : comment.getTimestamp())
            .likes(comment.getLikes())
            .build();

        commentRepository.save(row);

        return CommentResponse.builder()
            .id(row.getId())
            .author(row.getAuthor())
            .avatar(row.getAvatar())
            .content(row.getContent())
            .timestamp(row.getTimestamp())
            .likes(row.getLikes())
            .replies(List.of())
            .build();
    }

    public double getFloorPrice(String eventId, String ticketClass) {
        Optional<EventEntity> event = eventRepository.findById(parseUuid(eventId));
        if (event.isEmpty()) {
            return 0.0;
        }

        return parseMarketStats(event.get().getMarketStats()).stream()
            .filter(stat -> stat.getTicketClass().equalsIgnoreCase(ticketClass))
            .mapToDouble(MarketStatResponse::getFloorPrice)
            .findFirst()
            .orElse(0.0);
    }

    @Scheduled(fixedRate = 1500)
    public void streamRealtimeTicks() {
        List<EventEntity> events = eventRepository.findAll();
        long nowMillis = System.currentTimeMillis();

        for (EventEntity event : events) {
            List<MarketStatResponse> stats = parseMarketStats(event.getMarketStats());
            for (MarketStatResponse stat : stats) {
                String key = event.getId() + "|" + stat.getTicketClass();
                double base = stat.getFloorPrice() > 0 ? stat.getFloorPrice() : Math.max(1.0, stat.getOriginalPrice());
                double current = realtimePriceByKey.getOrDefault(key, base);

                double noise = ((nowMillis / 100 + key.hashCode()) % 1000) / 1000.0;
                double drift = (noise - 0.5) * Math.max(0.02, base * 0.01);
                double next = Math.max(0.0001, current + drift);

                realtimePriceByKey.put(key, next);

                PriceTickPayload payload = PriceTickPayload.builder()
                    .eventId(event.getId().toString())
                    .ticketClass(stat.getTicketClass())
                    .price(next)
                    .time(DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneOffset.UTC).format(Instant.now()))
                    .timestamp(Instant.now())
                    .build();

                priceWebSocketHandler.broadcast(payload);
            }
        }
    }

    private EventResponse toEventResponse(EventEntity event) {
        return EventResponse.builder()
            .id(event.getId().toString())
            .title(orEmpty(event.getTitle()))
            .coverImage(orEmpty(event.getImageUrl()))
            .location(orEmpty(event.getLocation()))
            .date(event.getStartTime() == null ? "" : DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneOffset.UTC).format(event.getStartTime()))
            .time(event.getStartTime() == null ? "" : DateTimeFormatter.ofPattern("hh:mm a").withZone(ZoneOffset.UTC).format(event.getStartTime()))
            .description(orEmpty(event.getDescription()))
            .about(orEmpty(event.getAbout()))
            .lineup(event.getLineup() == null ? List.of() : List.of(event.getLineup()))
            .organizer(orEmpty(event.getOrganizerName()))
            .tags(event.getTags() == null ? List.of() : List.of(event.getTags()))
            .tradingStatus(orEmpty(event.getTradingStatus()))
            .settlementDate(event.getSettlementDate() == null ? "" : DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneOffset.UTC).format(event.getSettlementDate()))
            .marketStats(parseMarketStats(event.getMarketStats()))
            .build();
    }

    private List<MarketStatResponse> parseMarketStats(String marketStatsJson) {
        if (marketStatsJson == null || marketStatsJson.isBlank()) {
            return List.of();
        }

        try {
            JsonNode root = objectMapper.readTree(marketStatsJson);
            if (!root.isArray()) {
                return List.of();
            }

            List<MarketStatResponse> result = new ArrayList<>();
            for (JsonNode node : root) {
                String ticketClass = node.path("ticketClass").asText("Regular");
                double originalPrice = node.path("originalPrice").asDouble(0.0);
                double floorPrice = node.path("floorPrice").asDouble(0.0);
                double change24h = node.path("change24h").asDouble(0.0);
                String volume24h = node.path("volume24h").asText("0 SUI");

                List<PricePointResponse> history = List.of(
                    PricePointResponse.builder().time("T-2h").price(originalPrice).build(),
                    PricePointResponse.builder().time("T-1h").price((originalPrice + floorPrice) / 2.0).build(),
                    PricePointResponse.builder().time("Now").price(floorPrice).build()
                );

                result.add(MarketStatResponse.builder()
                    .ticketClass(ticketClass)
                    .originalPrice(originalPrice)
                    .floorPrice(floorPrice)
                    .change24h(change24h)
                    .volume24h(volume24h)
                    .priceHistory(history)
                    .build());
            }
            return result;
        } catch (IOException exception) {
            return List.of();
        }
    }

    private String extractTicketClass(String metadataJson) {
        if (metadataJson == null || metadataJson.isBlank()) {
            return "Regular";
        }
        try {
            JsonNode node = objectMapper.readTree(metadataJson);
            return node.path("ticketClass").asText("Regular");
        } catch (IOException exception) {
            return "Regular";
        }
    }

    private UUID parseUuid(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid UUID eventId: " + value);
        }
    }

    private String orEmpty(String value) {
        return value == null ? "" : value;
    }
}
