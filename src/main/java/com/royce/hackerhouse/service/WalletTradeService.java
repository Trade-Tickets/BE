package com.royce.hackerhouse.service;

import com.royce.hackerhouse.dto.WalletDtos.RecordTradeRequest;
import com.royce.hackerhouse.dto.WalletDtos.TradeRecordResponse;
import com.royce.hackerhouse.dto.WalletDtos.WalletProfileResponse;
import com.royce.hackerhouse.entity.ProfileEntity;
import com.royce.hackerhouse.entity.WalletTradeEntity;
import com.royce.hackerhouse.repository.ProfileRepository;
import com.royce.hackerhouse.repository.WalletTradeRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletTradeService {

    private final WalletTradeRepository walletTradeRepository;
    private final ProfileRepository profileRepository;

    public TradeRecordResponse recordTrade(RecordTradeRequest request) {
        if (request.getWalletAddress() == null || request.getWalletAddress().isBlank()) {
            throw new IllegalArgumentException("walletAddress is required");
        }

        String wallet = request.getWalletAddress().trim();
        profileRepository.findFirstBySuiWalletAddress(wallet)
            .orElseGet(() -> profileRepository.save(ProfileEntity.builder()
                .id(UUID.randomUUID())
                .username("wallet_" + wallet.substring(0, Math.min(8, wallet.length())))
                .fullName("Wallet User")
                .avatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=" + wallet)
                .suiWalletAddress(wallet)
                .build()));

        String clientOrderId = request.getId() == null || request.getId().isBlank()
            ? "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()
            : request.getId();

        WalletTradeEntity entity = walletTradeRepository
            .findByWalletAddressAndClientOrderId(wallet, clientOrderId)
            .orElse(WalletTradeEntity.builder()
                .id(UUID.randomUUID())
                .walletAddress(wallet)
                .clientOrderId(clientOrderId)
                .createdAt(Instant.now())
                .build());

        entity.setEventId(defaultString(request.getEventId()));
        entity.setEventTitle(defaultString(request.getEventTitle()));
        entity.setTicketClass(defaultString(request.getTicketClass()));
        entity.setTradeType(defaultString(request.getTradeType()));
        entity.setPriceSui(BigDecimal.valueOf(request.getPriceSui() == null ? 0.0 : request.getPriceSui()));
        entity.setQuantity(request.getQuantity() == null ? 1 : request.getQuantity());
        entity.setTotalCost(BigDecimal.valueOf(request.getTotalCost() == null ? 0.0 : request.getTotalCost()));
        entity.setPlatformFee(BigDecimal.valueOf(request.getPlatformFee() == null ? 0.0 : request.getPlatformFee()));
        entity.setSellTax(BigDecimal.valueOf(request.getSellTax() == null ? 0.0 : request.getSellTax()));
        entity.setSuiTxDigest(request.getSuiTxDigest());
        entity.setStatus(defaultString(request.getStatus()));

        WalletTradeEntity saved = walletTradeRepository.save(entity);
        return toTrade(saved);
    }

    public List<TradeRecordResponse> getWalletTrades(String walletAddress) {
        return walletTradeRepository.findAllByWalletAddressOrderByCreatedAtDesc(walletAddress).stream()
            .map(this::toTrade)
            .toList();
    }

    public WalletProfileResponse getWalletProfile(String walletAddress) {
        List<TradeRecordResponse> trades = getWalletTrades(walletAddress);

        double totalBuy = trades.stream()
            .filter(t -> "buy".equalsIgnoreCase(t.getTradeType()) && "filled".equalsIgnoreCase(t.getStatus()))
            .mapToDouble(t -> t.getPriceSui() * t.getQuantity())
            .sum();

        double totalSell = trades.stream()
            .filter(t -> "sell".equalsIgnoreCase(t.getTradeType()) && "filled".equalsIgnoreCase(t.getStatus()))
            .mapToDouble(t -> t.getPriceSui() * t.getQuantity())
            .sum();

        Instant first = trades.isEmpty() ? Instant.now() : trades.get(trades.size() - 1).getCreatedAt();
        Instant last = trades.isEmpty() ? Instant.now() : trades.get(0).getCreatedAt();

        return WalletProfileResponse.builder()
            .walletAddress(walletAddress)
            .firstSeenAt(first)
            .lastActiveAt(last)
            .totalBuyVolume(totalBuy)
            .totalSellVolume(totalSell)
            .totalTrades(trades.size())
            .recentTrades(trades.stream().limit(20).toList())
            .build();
    }

    private TradeRecordResponse toTrade(WalletTradeEntity e) {
        return TradeRecordResponse.builder()
            .id(e.getClientOrderId())
            .walletAddress(e.getWalletAddress())
            .eventId(e.getEventId())
            .eventTitle(e.getEventTitle())
            .ticketClass(e.getTicketClass())
            .tradeType(e.getTradeType())
            .priceSui(e.getPriceSui() == null ? 0.0 : e.getPriceSui().doubleValue())
            .quantity(e.getQuantity() == null ? 0 : e.getQuantity())
            .totalCost(e.getTotalCost() == null ? 0.0 : e.getTotalCost().doubleValue())
            .platformFee(e.getPlatformFee() == null ? 0.0 : e.getPlatformFee().doubleValue())
            .sellTax(e.getSellTax() == null ? 0.0 : e.getSellTax().doubleValue())
            .suiTxDigest(e.getSuiTxDigest())
            .status(e.getStatus())
            .createdAt(e.getCreatedAt())
            .build();
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }
}
