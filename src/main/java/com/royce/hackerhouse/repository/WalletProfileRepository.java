package com.royce.hackerhouse.repository;

import com.royce.hackerhouse.entity.WalletProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletProfileRepository extends JpaRepository<WalletProfileEntity, String> {
}
