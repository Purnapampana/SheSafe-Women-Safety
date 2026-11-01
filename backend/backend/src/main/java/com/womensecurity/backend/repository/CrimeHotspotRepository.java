package com.womensecurity.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.womensecurity.backend.entity.CrimeHotspot;

public interface CrimeHotspotRepository extends JpaRepository<CrimeHotspot, Long> {

    List<CrimeHotspot> findByRiskLevel(String riskLevel);

}