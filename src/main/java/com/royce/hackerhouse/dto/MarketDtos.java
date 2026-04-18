package com.royce.hackerhouse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MarketDtos {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventResponse {
        @JsonProperty("id")
        private String id;
        @JsonProperty("title")
        private String title;
        @JsonProperty("coverImage")
        private String coverImage;
        @JsonProperty("location")
        private String location;
        @JsonProperty("date")
        private String date;
        @JsonProperty("time")
        private String time;
        @JsonProperty("description")
        private String description;
        @JsonProperty("about")
        private String about;
        @JsonProperty("lineup")
        private List<String> lineup;
        @JsonProperty("organizer")
        private String organizer;
        @JsonProperty("tags")
        private List<String> tags;
        @JsonProperty("tradingStatus")
        private String tradingStatus;
        @JsonProperty("settlementDate")
        private String settlementDate;
        @JsonProperty("marketStats")
        private List<MarketStatResponse> marketStats;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MarketStatResponse {
        private String ticketClass;
        private double originalPrice;
        private double floorPrice;
        private double change24h;
        private String volume24h;
        @Builder.Default
        private List<PricePointResponse> priceHistory = new ArrayList<>();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PricePointResponse {
        private String time;
        private double price;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketResponse {
        private String id;
        private String eventId;
        private String ticketClass;
        private double priceSui;
        private String status;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentResponse {
        private String id;
        private String author;
        private String avatar;
        private String content;
        private String timestamp;
        private int likes;
        @Builder.Default
        private List<CommentResponse> replies = new ArrayList<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCommentRequest {
        private String id;
        private String author;
        private String avatar;
        private String content;
        private String timestamp;
        private int likes;
        private List<CommentResponse> replies;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FloorPriceResponse {
        private double floorPrice;
    }
}
