package com.royce.hackerhouse.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events", schema = "public")
public class EventEntity {

    @Id
    private UUID id;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(columnDefinition = "TEXT")
    private String title;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String location;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String about;

    @Column(columnDefinition = "text[]")
    private String[] lineup;

    @Column(name = "organizer_id")
    private UUID organizerId;

    @Column(name = "organizer_name", columnDefinition = "TEXT")
    private String organizerName;

    @Column(columnDefinition = "text[]")
    private String[] tags;

    @Column(name = "trading_status", columnDefinition = "TEXT")
    private String tradingStatus;

    @Column(name = "settlement_date")
    private Instant settlementDate;

    @Column(name = "market_stats", columnDefinition = "jsonb")
    private String marketStats;
}
