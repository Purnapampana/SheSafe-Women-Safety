package com.womensecurity.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.womensecurity.backend.entity.MediaRecord;

public interface MediaRecordRepository
        extends JpaRepository<MediaRecord, Long> {

    List<MediaRecord> findByIncidentId(Long incidentId);

    List<MediaRecord>
    findByIncidentIdOrderByRecordedAtAsc(Long incidentId);

    List<MediaRecord> findByUserId(Long userId);
}