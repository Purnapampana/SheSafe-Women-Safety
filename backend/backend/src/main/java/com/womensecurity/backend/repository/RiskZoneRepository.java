package com.womensecurity.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.womensecurity.backend.entity.RiskZone;

public interface RiskZoneRepository
        extends JpaRepository<RiskZone, Long> {

    List<RiskZone> findByRiskLevel(
            String riskLevel
    );
}