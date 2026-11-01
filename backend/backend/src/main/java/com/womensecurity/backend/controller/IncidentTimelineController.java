package com.womensecurity.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.womensecurity.backend.dto.IncidentTimelineResponse;
import com.womensecurity.backend.entity.IncidentTimeline;
import com.womensecurity.backend.service.IncidentTimelineService;

@RestController
@RequestMapping("/api/incidents")
@CrossOrigin("*")
public class IncidentTimelineController {

    @Autowired
    private IncidentTimelineService incidentTimelineService;

    // ==========================================
    // CREATE INCIDENT
    // ==========================================

    @PostMapping("/create/{userId}")
    public ResponseEntity<IncidentTimeline>
            createIncident(
                    @PathVariable Long userId) {

        IncidentTimeline incident =
                incidentTimelineService
                        .createIncident(userId);

        return ResponseEntity.ok(incident);
    }
@GetMapping("/user/{userId}")
public ResponseEntity<List<IncidentTimeline>> getUserIncidents(
        @PathVariable Long userId) {

    return ResponseEntity.ok(
            incidentTimelineService.getIncidentsByUserId(userId)
    );
}
    // ==========================================
    // GET INCIDENT
    // ==========================================

    @GetMapping("/{incidentId}")
    public ResponseEntity<IncidentTimeline>
            getIncident(
                    @PathVariable Long incidentId) {

        IncidentTimeline incident =
                incidentTimelineService
                        .getIncident(incidentId);

        if (incident == null) {

            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(incident);
    }

    // ==========================================
    // GET COMPLETE TIMELINE
    // ==========================================

    @GetMapping("/{incidentId}/timeline")
    public ResponseEntity<IncidentTimelineResponse>
            getCompleteTimeline(
                    @PathVariable Long incidentId) {

        IncidentTimelineResponse response =
                incidentTimelineService
                        .getCompleteTimeline(
                                incidentId);

        if (response == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // UPDATE RESPONSE STATUS
    // ==========================================

    @PutMapping("/{incidentId}/status")
    public ResponseEntity<IncidentTimeline>
            updateStatus(
                    @PathVariable Long incidentId,
                    @RequestParam String status) {

        IncidentTimeline incident =
                incidentTimelineService
                        .updateResponseStatus(
                                incidentId,
                                status);

        if (incident == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(incident);
    }
}