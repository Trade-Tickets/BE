package com.royce.hackerhouse.service;

import com.royce.hackerhouse.dto.WalletDtos.RecordTradeRequest;
import com.royce.hackerhouse.dto.WalletDtos.TradeRecordResponse;
import com.royce.hackerhouse.dto.WalletDtos.WalletProfileResponse;
import com.royce.hackerhouse.entity.TradeHistoryEntity;
import com.royce.hackerhouse.entity.WalletProfileEntity;
import com.royce.hackerhouse.repository.TradeHistoryRepository;
import com.royce.hackerhouse.repository.WalletProfileRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletProfileRepository walletProfileRepository;
    private final TradeHistoryRepository tradeHistoryRepository;

    // ── GET wallet profile + recent trades ───────────────────────────────────
    public WalletProfileResponse getProfile(String walletAddress) {
        WalletProfileEntity profile = walletProfileRepository.findById(walletAddress)
            .orElseGet(() -> createBlankProfile(walletAddress));

        List<TradeHistoryEntity> trades =
            tradeHistoryRepository.findAllByWalletAddressOrderByCreatedAtDesc(walletAddress);

        return WalletProfileResponse.builder()
            .walletAddress(profile.getWalletAddress())
            .firstSeenAt(profile.getFirstSeenAt())
            .lastActiveAt(profile.getLastActiveAt())
            .totalBuyVolume(toDouble(profile.getTotalBuyVolume()))
            .totalSellVolume(toDouble(profile.getTotalSellVolume()))
            .totalTrades(profile.getTotalTrades())
            .recentTrades(trades.stream().map(this::toResponse).toList())
            .build();
    }

    // ── POST record a new trade ───────────────────────────────────────────────
    @Transactional
    public TradeRecordResponse recordTrade(RecordTradeRequest req) {
        String wallet = req.getWalletAddress();
        Instant now = Instant.now();

        // Upsert wallet profile
        WalletProfileEntity profile = walletProfileRepository.findById(wallet)
            .orElseGet(() -> createBlankProfile(wallet));
        profile.setLastActiveAt(now);
        profile.setTotalTrades(profile.getTotalTrades() + 1);

        BigDecimal cost = toBigDecimal(req.getTotalCost());
        if ("buy".equalsIgnoreCase(req.getTradeType())) {
            BigDecimal prev = profile.getTotalBuyVolume() != null ? profile.getTotalBuyVolume() : BigDecimal.ZERO;
            profile.setTotalBuyVolume(prev.add(cost));
        } else {
            BigDecimal prev = profile.getTotalSellVolume() != null ? profile.getTotalSellVolume() : BigDecimal.ZERO;
            profile.setTotalSellVolume(prev.add(cost));
        }
        walletProfileRepository.save(profile);

        // Persist trade record
        TradeHistoryEntity trade = TradeHistoryEntity.builder()
            .id(UUID.randomUUID())
            .walletAddress(wallet)
            .eventId(req.getEventId())
            .eventTitle(req.getEventTitle())
            .ticketClass(req.getTicketClass())
            .tradeType(req.getTradeType())
            .priceSui(toBigDecimal(req.getPriceSui()))
            .quantity(req.getQuantity() != null ? req.getQuantity() : 0)
            .totalCost(cost)
            .platformFee(toBigDecimal(req.getPlatformFee()))
            .sellTax(toBigDecimal(req.getSellTax()))
            .suiTxDigest(req.getSuiTxDigest())
            .status(req.getStatus() != null ? req.getStatus() : "filled")
            .createdAt(now)
            .build();
        tradeHistoryRepository.save(trade);

        return toResponse(trade);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private WalletProfileEntity createBlankProfile(String walletAddress) {
        Instant now = Instant.now();
        return WalletProfileEntity.builder()
            .walletAddress(walletAddress)
            .firstSeenAt(now)
            .lastActiveAt(now)
            .totalBuyVolume(BigDecimal.ZERO)
            .totalSellVolume(BigDecimal.ZERO)
            .totalTrades(0)
            .build();
    }

    private TradeRecordResponse toResponse(TradeHistoryEntity e) {
        return TradeRecordResponse.builder()
            .id(e.getId().toString())
            .walletAddress(e.getWalletAddress())
            .eventId(e.getEventId())
            .eventTitle(e.getEventTitle())
            .ticketClass(e.getTicketClass())
            .tradeType(e.getTradeType())
            .priceSui(toDouble(e.getPriceSui()))
            .quantity(e.getQuantity())
            .totalCost(toDouble(e.getTotalCost()))
            .platformFee(toDouble(e.getPlatformFee()))
            .sellTax(toDouble(e.getSellTax()))
            .suiTxDigest(e.getSuiTxDigest())
            .status(e.getStatus())
            .createdAt(e.getCreatedAt())
            .build();
    }

    private static Double toDouble(BigDecimal bd) {
        return bd != null ? bd.doubleValue() : null;
    }

    private static BigDecimal toBigDecimal(Double d) {
        return d != null ? BigDecimal.valueOf(d) : BigDecimal.ZERO;
    }
}
