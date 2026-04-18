package com.royce.hackerhouse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class WalletDtos {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecordTradeRequest {
        @JsonProperty("id")
        private String id;
        @JsonProperty("walletAddress")
        private String walletAddress;
        @JsonProperty("eventId")
        private String eventId;
        @JsonProperty("eventTitle")
        private String eventTitle;
        @JsonProperty("ticketClass")
        private String ticketClass;
        @JsonProperty("tradeType")
        private String tradeType;
        @JsonProperty("priceSui")
        private Double priceSui;
        @JsonProperty("quantity")
        private Integer quantity;
        @JsonProperty("totalCost")
        private Double totalCost;
        @JsonProperty("platformFee")
        private Double platformFee;
        @JsonProperty("sellTax")
        private Double sellTax;
        @JsonProperty("suiTxDigest")
        private String suiTxDigest;
        @JsonProperty("status")
        private String status;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TradeRecordResponse {
        @JsonProperty("id")
        private String id;
        @JsonProperty("walletAddress")
        private String walletAddress;
        @JsonProperty("eventId")
        private String eventId;
        @JsonProperty("eventTitle")
        private String eventTitle;
        @JsonProperty("ticketClass")
        private String ticketClass;
        @JsonProperty("tradeType")
        private String tradeType;
        @JsonProperty("priceSui")
        private Double priceSui;
        @JsonProperty("quantity")
        private Integer quantity;
        @JsonProperty("totalCost")
        private Double totalCost;
        @JsonProperty("platformFee")
        private Double platformFee;
        @JsonProperty("sellTax")
        private Double sellTax;
        @JsonProperty("suiTxDigest")
        private String suiTxDigest;
        @JsonProperty("status")
        private String status;
        @JsonProperty("createdAt")
        private Instant createdAt;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WalletProfileResponse {
        @JsonProperty("walletAddress")
        private String walletAddress;
        @JsonProperty("firstSeenAt")
        private Instant firstSeenAt;
        @JsonProperty("lastActiveAt")
        private Instant lastActiveAt;
        @JsonProperty("totalBuyVolume")
        private Double totalBuyVolume;
        @JsonProperty("totalSellVolume")
        private Double totalSellVolume;
        @JsonProperty("totalTrades")
        private Integer totalTrades;
        @JsonProperty("recentTrades")
        private List<TradeRecordResponse> recentTrades;
    }
}
