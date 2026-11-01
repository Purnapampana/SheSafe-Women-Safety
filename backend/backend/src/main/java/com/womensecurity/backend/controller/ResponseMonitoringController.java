package com.womensecurity.backend.controller;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.womensecurity.backend.entity.IncidentTimeline;
import com.womensecurity.backend.model.ResponseMonitoringResponse;
import com.womensecurity.backend.repository.IncidentTimelineRepository;

@RestController
@RequestMapping("/api/staff/response-monitoring")
@CrossOrigin("*")
public class ResponseMonitoringController {

    @Autowired
    private IncidentTimelineRepository incidentTimelineRepository;


    @GetMapping
    @PreAuthorize(
        "hasAnyRole(" +
        "'SUPER_ADMIN'," +
        "'POLICE_SUPERVISOR'," +
        "'POLICE_OFFICER'," +
        "'POLICE_FIELD_RESPONDER'," +
        "'WOMEN_HELPLINE_OPERATOR'" +
        ")"
    )
    public ResponseEntity<ResponseMonitoringResponse>
    getResponseMonitoring() {

        List<IncidentTimeline> incidents =
                incidentTimelineRepository.findAll();

        long resolved = 0;
        long active = 0;
        long pendingResponse = 0;

        double totalResolutionMinutes = 0;
        int resolvedWithTime = 0;


        for (IncidentTimeline incident : incidents) {

            String status =
                    incident.getEmergencyResponseStatus();

            if (status == null) {
                continue;
            }

            String normalizedStatus =
                    status.trim().toUpperCase();


            // ==========================================
            // RESOLVED
            // ==========================================

            if ("RESOLVED".equals(normalizedStatus)) {

                resolved++;

                if (incident.getSosActivationTime() != null
                        && incident.getResolvedAt() != null) {

                    long seconds =
                            Duration.between(
                                incident.getSosActivationTime(),
                                incident.getResolvedAt()
                            ).getSeconds();

                    if (seconds >= 0) {

                        totalResolutionMinutes +=
                                seconds / 60.0;

                        resolvedWithTime++;
                    }
                }

                continue;
            }


            // ==========================================
            // PENDING RESPONSE
            // ==========================================

            if ("PENDING".equals(normalizedStatus)
                    || "PENDING_RESPONSE".equals(normalizedStatus)
                    || "WAITING_FOR_RESPONSE".equals(normalizedStatus)) {

                pendingResponse++;
                continue;
            }


            // ==========================================
            // ACTIVE
            // ==========================================

            if ("ACTIVE".equals(normalizedStatus)) {
                active++;
            }
        }


        ResponseMonitoringResponse response =
                new ResponseMonitoringResponse();

        response.setTotalIncidents(
                incidents.size());

        response.setResolved(resolved);

        response.setActive(active);

        response.setPendingResponse(
                pendingResponse);


        if (resolvedWithTime > 0) {

            double average =
                    totalResolutionMinutes
                    / resolvedWithTime;

            response.setAverageResolutionMinutes(
                    Math.round(average * 100.0) / 100.0);

        } else {

            response.setAverageResolutionMinutes(
                    0.0);
        }


        return ResponseEntity.ok(response);
    }
}