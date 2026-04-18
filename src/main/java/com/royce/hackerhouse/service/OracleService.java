package com.royce.hackerhouse.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.royce.hackerhouse.dto.PriceResponse;
import com.royce.hackerhouse.entity.OraclePriceLog;
import com.royce.hackerhouse.repository.OraclePriceRepository;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OracleService {

    private static final String DEFAULT_PAIR = "SUI/USD";
    private static final String ALGORITHM = "Median Filter";

    private final OraclePriceRepository oraclePriceRepository;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 30000)
    public void aggregateAndSavePrice() {
        CompletableFuture<PriceResponse.SourceDto> binanceFuture = CompletableFuture.supplyAsync(() ->
            new PriceResponse.SourceDto("Binance", 1.2400, "HEALTHY")
        );
        CompletableFuture<PriceResponse.SourceDto> okxFuture = CompletableFuture.supplyAsync(() ->
            new PriceResponse.SourceDto("OKX", 1.2600, "HEALTHY")
        );
        CompletableFuture<PriceResponse.SourceDto> bybitFuture = CompletableFuture.supplyAsync(() ->
            new PriceResponse.SourceDto("Bybit", 0.0500, "HEALTHY")
        );

        List<PriceResponse.SourceDto> sources = CompletableFuture
            .allOf(binanceFuture, okxFuture, bybitFuture)
            .thenApply(ignored -> List.of(binanceFuture.join(), okxFuture.join(), bybitFuture.join()))
            .join();

        double medianPrice = calculateMedian(sources);
        List<PriceResponse.SourceDto> evaluatedSources = applyAnomalyDetection(sources, medianPrice);

        try {
            OraclePriceLog logRecord = OraclePriceLog.builder()
                .pair(DEFAULT_PAIR)
                .verifiedPrice(medianPrice)
                .sourcesJson(objectMapper.writeValueAsString(evaluatedSources))
                .algorithm(ALGORITHM)
                .timestamp(Instant.now())
                .build();

            oraclePriceRepository.save(logRecord);
            log.info("Saved oracle price log with median={}", medianPrice);
        } catch (IOException exception) {
            log.error("Failed to serialize source data", exception);
        }
    }

    public PriceResponse getLatestPrice() {
        Optional<OraclePriceLog> latest = oraclePriceRepository.findFirstByOrderByTimestampDesc();

        if (latest.isEmpty()) {
            return PriceResponse.builder()
                .pair(DEFAULT_PAIR)
                .verifiedPrice(null)
                .sources(List.of())
                .algorithm(ALGORITHM)
                .timestamp(Instant.now())
                .build();
        }

        OraclePriceLog logRecord = latest.get();
        return PriceResponse.builder()
            .pair(logRecord.getPair())
            .verifiedPrice(logRecord.getVerifiedPrice())
            .sources(parseSources(logRecord.getSourcesJson()))
            .algorithm(logRecord.getAlgorithm())
            .timestamp(logRecord.getTimestamp())
            .build();
    }

    private double calculateMedian(List<PriceResponse.SourceDto> sources) {
        List<Double> sortedPrices = sources.stream()
            .map(PriceResponse.SourceDto::getPrice)
            .sorted(Comparator.naturalOrder())
            .toList();

        return sortedPrices.get(1);
    }

    private List<PriceResponse.SourceDto> applyAnomalyDetection(List<PriceResponse.SourceDto> sources, double median) {
        List<PriceResponse.SourceDto> evaluated = new ArrayList<>();
        for (PriceResponse.SourceDto source : sources) {
            double deviation = Math.abs(source.getPrice() - median) / median;
            String status = deviation > 0.05 ? "ANOMALY_EXCLUDED" : "HEALTHY";
            evaluated.add(new PriceResponse.SourceDto(source.getName(), source.getPrice(), status));
        }
        return evaluated;
    }

    private List<PriceResponse.SourceDto> parseSources(String sourcesJson) {
        try {
            return objectMapper.readValue(sourcesJson, new TypeReference<>() {
            });
        } catch (IOException exception) {
            log.error("Failed to parse sources JSON", exception);
            return List.of();
        }
    }
}
