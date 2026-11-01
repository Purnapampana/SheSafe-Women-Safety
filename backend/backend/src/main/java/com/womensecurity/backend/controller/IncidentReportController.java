package com.womensecurity.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.womensecurity.backend.entity.IncidentReport;
import com.womensecurity.backend.service.IncidentReportService;

@RestController
@RequestMapping("/api/incident-reports")
@CrossOrigin(origins = "*")
public class IncidentReportController {

    private final IncidentReportService incidentReportService;

    public IncidentReportController(
            IncidentReportService incidentReportService) {

        this.incidentReportService =
                incidentReportService;
    }

    @PostMapping
    public ResponseEntity<IncidentReport> createReport(
            @RequestBody IncidentReport report) {

        return ResponseEntity.ok(
                incidentReportService.createReport(
                        report
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<IncidentReport>>
    getAllReports() {

        return ResponseEntity.ok(
                incidentReportService.getAllReports()
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<IncidentReport>>
    getReportsByUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                incidentReportService
                        .getReportsByUser(userId)
        );
    }

    @GetMapping("/type/{incidentType}")
    public ResponseEntity<List<IncidentReport>>
    getReportsByType(
            @PathVariable String incidentType) {

        return ResponseEntity.ok(
                incidentReportService
                        .getReportsByType(incidentType)
        );
    }
}