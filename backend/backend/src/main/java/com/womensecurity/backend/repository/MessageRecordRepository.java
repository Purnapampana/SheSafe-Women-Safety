package com.womensecurity.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.womensecurity.backend.entity.MessageRecord;

public interface MessageRecordRepository
        extends JpaRepository<MessageRecord, Long> {

    List<MessageRecord> findByIncidentId(Long incidentId);

    List<MessageRecord>
    findByIncidentIdOrderBySentAtAsc(Long incidentId);

    List<MessageRecord> findByUserId(Long userId);
}