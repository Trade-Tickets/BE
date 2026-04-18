package com.royce.hackerhouse.repository;

import com.royce.hackerhouse.entity.MarketStatEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketStatRepository extends JpaRepository<MarketStatEntity, Long> {

    List<MarketStatEntity> findAllByEventId(String eventId);

    Optional<MarketStatEntity> findFirstByEventIdAndTicketClassIgnoreCase(String eventId, String ticketClass);
}
