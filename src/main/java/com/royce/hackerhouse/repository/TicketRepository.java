package com.royce.hackerhouse.repository;

import com.royce.hackerhouse.entity.TicketEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<TicketEntity, UUID> {

    List<TicketEntity> findAllByEventId(UUID eventId);
}
