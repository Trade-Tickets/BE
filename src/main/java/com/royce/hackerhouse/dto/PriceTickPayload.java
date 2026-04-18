package com.royce.hackerhouse.dto;

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
public class PriceTickPayload {
    private String eventId;
    private String ticketClass;
    private double price;
    private String time;
    private Instant timestamp;
}
