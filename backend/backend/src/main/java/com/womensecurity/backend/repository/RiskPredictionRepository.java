package com.womensecurity.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.womensecurity.backend.entity.RiskPredictionLog;

public interface RiskPredictionRepository
        extends JpaRepository<RiskPredictionLog, Long> {

}