package com.womensecurity.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.womensecurity.backend.service.EmailService;

@RestController
@RequestMapping("/api/email")
@CrossOrigin("*")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-sos")
    public ResponseEntity<String> sendSOSEmail(
            @RequestBody SOSEmailRequest request) {

        try {

            emailService.sendSOSAlert(
                    request.getUserId(),
                    request.getIncidentId(),
                    request.getLatitude(),
                    request.getLongitude()
            );

            return ResponseEntity.ok(
                    "SOS email sent successfully"
            );

        } catch (Exception e) {

            return ResponseEntity
                    .internalServerError()
                    .body(
                        "Failed to send SOS email: "
                        + e.getMessage()
                    );
        }
    }

    public static class SOSEmailRequest {

        private Long userId;
        private Long incidentId;
        private Double latitude;
        private Double longitude;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getIncidentId() {
            return incidentId;
        }

        public void setIncidentId(Long incidentId) {
            this.incidentId = incidentId;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }
    }
}