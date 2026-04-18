package com.royce.hackerhouse.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
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
@Table(name = "oracle_price_logs")
public class OraclePriceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(nullable = false)
    private String pair = "SUI/USD";

    @Column(name = "verified_price", nullable = false)
    private Double verifiedPrice;

    @Column(name = "sources_json", columnDefinition = "TEXT", nullable = false)
    private String sourcesJson;

    @Column(nullable = false)
    private String algorithm;

    @Column(nullable = false, updatable = false)
    private Instant timestamp;

    @PrePersist
    public void prePersist() {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
        if (pair == null || pair.isBlank()) {
            pair = "SUI/USD";
        }
    }
}
