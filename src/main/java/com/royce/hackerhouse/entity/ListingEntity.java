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
@Table(name = "listings", schema = "public")
public class ListingEntity {

    @Id
    private UUID id;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "ticket_id", nullable = false, unique = true)
    private UUID ticketId;

    @Column(name = "seller_id", nullable = false)
    private UUID sellerId;

    @Column(nullable = false, precision = 18, scale = 9)
    private BigDecimal price;
}
