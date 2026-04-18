package com.royce.hackerhouse.repository;

import com.royce.hackerhouse.entity.ProfileEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<ProfileEntity, UUID> {

    Optional<ProfileEntity> findFirstBySuiWalletAddress(String suiWalletAddress);
}
