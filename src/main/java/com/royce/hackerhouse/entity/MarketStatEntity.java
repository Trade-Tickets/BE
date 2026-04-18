package com.royce.hackerhouse.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "market_stats")
public class MarketStatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "ticket_class", nullable = false)
    private String ticketClass;

    @Column(name = "original_price", nullable = false)
    private double originalPrice;

    @Column(name = "floor_price", nullable = false)
    private double floorPrice;

    @Column(name = "change_24h", nullable = false)
    private double change24h;

    @Column(name = "volume_24h", nullable = false)
    private String volume24h;

    @Column(name = "price_history_json", nullable = false, columnDefinition = "TEXT")
    private String priceHistoryJson;
}
