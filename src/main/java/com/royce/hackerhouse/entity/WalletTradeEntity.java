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
@Table(name = "wallet_trades", schema = "public")
public class WalletTradeEntity {

    @Id
    private UUID id;

    @Column(name = "wallet_address", nullable = false)
    private String walletAddress;

    @Column(name = "client_order_id", nullable = false)
    private String clientOrderId;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "event_title", nullable = false)
    private String eventTitle;

    @Column(name = "ticket_class", nullable = false)
    private String ticketClass;

    @Column(name = "trade_type", nullable = false)
    private String tradeType;

    @Column(name = "price_sui", nullable = false, precision = 18, scale = 9)
    private BigDecimal priceSui;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "total_cost", nullable = false, precision = 18, scale = 9)
    private BigDecimal totalCost;

    @Column(name = "platform_fee", nullable = false, precision = 18, scale = 9)
    private BigDecimal platformFee;

    @Column(name = "sell_tax", nullable = false, precision = 18, scale = 9)
    private BigDecimal sellTax;

    @Column(name = "sui_tx_digest")
    private String suiTxDigest;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
