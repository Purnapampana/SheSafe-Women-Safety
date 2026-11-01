package com.womensecurity.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.womensecurity.backend.entity.EmergencyTracking;

public interface EmergencyTrackingRepository
        extends JpaRepository<EmergencyTracking, Long> {

    Optional<EmergencyTracking> findFirstByUserIdOrderByStartedAtDesc(Long userId);

}