package com.womensecurity.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.womensecurity.backend.entity.ThreatDetection;

public interface ThreatRepository extends JpaRepository<ThreatDetection, Long> {

    List<ThreatDetection> findByUserId(Long userId);

    Optional<ThreatDetection> findFirstByUserIdOrderByCreatedAtDesc(Long userId);

}