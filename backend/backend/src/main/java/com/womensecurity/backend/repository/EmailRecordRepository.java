package com.womensecurity.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.womensecurity.backend.entity.EmailRecord;

public interface EmailRecordRepository
        extends JpaRepository<EmailRecord, Long> {

    List<EmailRecord> findByIncidentId(Long incidentId);

    List<EmailRecord> findByIncidentIdOrderBySentAtAsc(Long incidentId);

    List<EmailRecord> findByUserId(Long userId);
}