package com.royce.hackerhouse.repository;

import com.royce.hackerhouse.entity.ListingEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingRepository extends JpaRepository<ListingEntity, UUID> {

    List<ListingEntity> findAllBySellerId(UUID sellerId);

    Optional<ListingEntity> findByTicketId(UUID ticketId);
}
