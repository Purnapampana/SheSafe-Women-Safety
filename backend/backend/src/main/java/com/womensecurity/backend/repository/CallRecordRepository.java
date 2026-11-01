package com.womensecurity.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.womensecurity.backend.entity.CallRecord;

public interface CallRecordRepository
        extends JpaRepository<CallRecord, Long> {

    List<CallRecord> findByIncidentId(Long incidentId);

    List<CallRecord>
    findByIncidentIdOrderByCallTimeAsc(Long incidentId);

    List<CallRecord> findByUserId(Long userId);
}