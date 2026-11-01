package com.womensecurity.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.womensecurity.backend.entity.IncidentReport;

public interface IncidentReportRepository
        extends JpaRepository<IncidentReport, Long> {

    List<IncidentReport> findByUserId(Long userId);

    List<IncidentReport> findByIncidentType(
            String incidentType
    );
}