package com.royce.hackerhouse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;
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
public class PriceResponse {

    private String pair;

    @JsonProperty("verified_price")
    private Double verifiedPrice;

    private List<SourceDto> sources;

    private String algorithm;

    private Instant timestamp;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SourceDto {
        private String name;
        private Double price;
        private String status;
    }
}
