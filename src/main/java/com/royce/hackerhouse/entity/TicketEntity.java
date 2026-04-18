package com.royce.hackerhouse.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
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
@Table(name = "tickets", schema = "public")
public class TicketEntity {

    public enum TicketStatus {
        available,
        sold,
        listing
    }

    @Id
    private UUID id;

    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "owner_id")
    private UUID ownerId;

    @Column(name = "sui_object_id", columnDefinition = "TEXT")
    private String suiObjectId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ticket_status")
    private TicketStatus status;

    @Column(name = "price_sui", precision = 18, scale = 9)
    private BigDecimal priceSui;

    @Column(columnDefinition = "jsonb")
    private String metadata;
}
