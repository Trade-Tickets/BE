package com.royce.hackerhouse.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
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
@Table(name = "trade_history", schema = "public")
public class TradeHistoryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "wallet_address", columnDefinition = "TEXT", nullable = false)
    private String walletAddress;

    @Column(name = "event_id", columnDefinition = "TEXT")
    private String eventId;

    @Column(name = "event_title", columnDefinition = "TEXT")
    private String eventTitle;

    @Column(name = "ticket_class", columnDefinition = "TEXT")
    private String ticketClass;

    /** "buy" or "sell" */
    @Column(name = "trade_type", columnDefinition = "TEXT", nullable = false)
    private String tradeType;

    @Column(name = "price_sui", columnDefinition = "NUMERIC(18,9)")
    private BigDecimal priceSui;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "total_cost", columnDefinition = "NUMERIC(18,9)")
    private BigDecimal totalCost;

    @Column(name = "platform_fee", columnDefinition = "NUMERIC(18,9)")
    private BigDecimal platformFee;

    @Column(name = "sell_tax", columnDefinition = "NUMERIC(18,9)")
    private BigDecimal sellTax;

    /** On-chain tx digest from Sui network */
    @Column(name = "sui_tx_digest", columnDefinition = "TEXT")
    private String suiTxDigest;

    /** "filled" | "open" | "cancelled" */
    @Column(name = "status", columnDefinition = "TEXT")
    private String status;

    @Column(name = "created_at")
    private Instant createdAt;
}
