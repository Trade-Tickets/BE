package com.royce.hackerhouse.repository;

import com.royce.hackerhouse.entity.TradeHistoryEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeHistoryRepository extends JpaRepository<TradeHistoryEntity, UUID> {

    List<TradeHistoryEntity> findAllByWalletAddressOrderByCreatedAtDesc(String walletAddress);
}
