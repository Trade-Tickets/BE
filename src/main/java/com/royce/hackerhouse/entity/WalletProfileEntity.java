package com.royce.hackerhouse.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "wallet_profiles", schema = "public")
public class WalletProfileEntity {

    @Id
    @Column(name = "wallet_address", columnDefinition = "TEXT")
    private String walletAddress;

    @Column(name = "first_seen_at")
    private Instant firstSeenAt;

    @Column(name = "last_active_at")
    private Instant lastActiveAt;

    @Column(name = "total_buy_volume", columnDefinition = "NUMERIC(18,9)")
    private java.math.BigDecimal totalBuyVolume;

    @Column(name = "total_sell_volume", columnDefinition = "NUMERIC(18,9)")
    private java.math.BigDecimal totalSellVolume;

    @Column(name = "total_trades")
    private int totalTrades;
}
