package com.royce.hackerhouse.repository;

import com.royce.hackerhouse.entity.EventEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<EventEntity, UUID> {
}
