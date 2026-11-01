package com.womensecurity.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.womensecurity.backend.entity.IncidentReport;
import com.womensecurity.backend.repository.IncidentReportRepository;

@Service
public class IncidentReportService {

    private final IncidentReportRepository incidentReportRepository;

    public IncidentReportService(
            IncidentReportRepository incidentReportRepository) {

        this.incidentReportRepository =
                incidentReportRepository;
    }

    public IncidentReport createReport(
            IncidentReport report) {

        if (report.getReportedAt() == null) {
            report.setReportedAt(
                    LocalDateTime.now()
            );
        }

        if (report.getStatus() == null ||
                report.getStatus().trim().isEmpty()) {

            report.setStatus("REPORTED");
        }

        return incidentReportRepository.save(report);
    }

    public List<IncidentReport> getAllReports() {

        return incidentReportRepository.findAll();
    }

    public List<IncidentReport> getReportsByUser(
            Long userId) {

        return incidentReportRepository
                .findByUserId(userId);
    }

    public List<IncidentReport> getReportsByType(
            String incidentType) {

        return incidentReportRepository
                .findByIncidentType(incidentType);
    }
}