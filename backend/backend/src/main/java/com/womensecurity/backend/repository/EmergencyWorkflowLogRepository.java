package com.womensecurity.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.womensecurity.backend.entity.EmergencyWorkflowLog;

public interface EmergencyWorkflowLogRepository
        extends JpaRepository<EmergencyWorkflowLog, Long> {

}