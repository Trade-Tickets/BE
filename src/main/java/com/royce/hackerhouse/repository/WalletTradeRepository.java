package com.royce.hackerhouse.repository;

import com.royce.hackerhouse.entity.WalletTradeEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletTradeRepository extends JpaRepository<WalletTradeEntity, UUID> {

    List<WalletTradeEntity> findAllByWalletAddressOrderByCreatedAtDesc(String walletAddress);

    Optional<WalletTradeEntity> findByWalletAddressAndClientOrderId(String walletAddress, String clientOrderId);
}
