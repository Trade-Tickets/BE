package com.royce.hackerhouse.repository;

import com.royce.hackerhouse.entity.OraclePriceLog;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OraclePriceRepository extends JpaRepository<OraclePriceLog, Long> {

    Optional<OraclePriceLog> findFirstByOrderByTimestampDesc();
}
