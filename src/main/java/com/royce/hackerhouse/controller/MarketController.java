package com.royce.hackerhouse.controller;

import com.royce.hackerhouse.dto.MarketDtos.CommentResponse;
import com.royce.hackerhouse.dto.MarketDtos.CreateCommentRequest;
import com.royce.hackerhouse.dto.MarketDtos.EventResponse;
import com.royce.hackerhouse.dto.MarketDtos.FloorPriceResponse;
import com.royce.hackerhouse.dto.MarketDtos.TicketResponse;
import com.royce.hackerhouse.service.MarketDataService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
@RequiredArgsConstructor
public class MarketController {

    private final MarketDataService marketDataService;

    @GetMapping("/events")
    public List<EventResponse> getEvents(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "6") int size
    ) {
        return marketDataService.getEvents(page, size);
    }

    @GetMapping("/events/{id}")
    public EventResponse getEventById(@PathVariable("id") String id) {
        return marketDataService.getEventById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
    }

    @GetMapping("/tickets")
    public List<TicketResponse> getTicketsByEvent(@RequestParam("eventId") String eventId) {
        return marketDataService.getTicketsByEvent(eventId);
    }

    @GetMapping("/events/{id}/comments")
    public List<CommentResponse> getComments(@PathVariable("id") String id) {
        return marketDataService.getCommentsByEvent(id);
    }

    @PostMapping("/events/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse addComment(
        @PathVariable("id") String id,
        @RequestBody CreateCommentRequest request
    ) {
        CommentResponse comment = CommentResponse.builder()
            .id(request.getId())
            .author(request.getAuthor())
            .avatar(request.getAvatar())
            .content(request.getContent())
            .timestamp(request.getTimestamp())
            .likes(request.getLikes())
            .replies(request.getReplies() == null ? List.of() : request.getReplies())
            .build();
        return marketDataService.addComment(id, comment);
    }

    @GetMapping("/market/floor-price")
    public FloorPriceResponse getFloorPrice(
        @RequestParam("eventId") String eventId,
        @RequestParam("ticketClass") String ticketClass
    ) {
        double floorPrice = marketDataService.getFloorPrice(eventId, ticketClass);
        return FloorPriceResponse.builder().floorPrice(floorPrice).build();
    }
}
