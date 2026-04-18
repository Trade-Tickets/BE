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
@Table(name = "transactions", schema = "public")
public class TransactionEntity {

    @Id
    private UUID id;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "buyer_id")
    private UUID buyerId;

    @Column(name = "seller_id")
    private UUID sellerId;

    @Column(name = "ticket_id", nullable = false)
    private UUID ticketId;

    @Column(nullable = false, precision = 18, scale = 9)
    private BigDecimal amount;

    @Column(name = "platform_fee", precision = 18, scale = 9)
    private BigDecimal platformFee;

    @Column(name = "sell_tax", precision = 18, scale = 9)
    private BigDecimal sellTax;

    @Column(name = "sui_transaction_digest", nullable = false, unique = true)
    private String suiTransactionDigest;
}
