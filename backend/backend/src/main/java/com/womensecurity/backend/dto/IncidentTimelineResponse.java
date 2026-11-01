package com.womensecurity.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class IncidentTimelineResponse {

    private Long incidentId;
private Long incidentNumber;
    private Long userId;

    private LocalDateTime sosActivationTime;

    private String emergencyResponseStatus;

    private LocalDateTime resolvedAt;

    private List<TimelineEvent> timeline;


    public IncidentTimelineResponse() {
    }


    public Long getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(Long incidentId) {
        this.incidentId = incidentId;
    }
public Long getIncidentNumber() {
    return incidentNumber;
}

public void setIncidentNumber(
        Long incidentNumber) {

    this.incidentNumber =
            incidentNumber;
}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    public LocalDateTime getSosActivationTime() {
        return sosActivationTime;
    }

    public void setSosActivationTime(
            LocalDateTime sosActivationTime) {

        this.sosActivationTime =
                sosActivationTime;
    }


    public String getEmergencyResponseStatus() {
        return emergencyResponseStatus;
    }

    public void setEmergencyResponseStatus(
            String emergencyResponseStatus) {

        this.emergencyResponseStatus =
                emergencyResponseStatus;
    }


    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(
            LocalDateTime resolvedAt) {

        this.resolvedAt = resolvedAt;
    }


    public List<TimelineEvent> getTimeline() {
        return timeline;
    }

    public void setTimeline(
            List<TimelineEvent> timeline) {

        this.timeline = timeline;
    }


    // =====================================================
    // TIMELINE EVENT
    // =====================================================

    public static class TimelineEvent {

        private String type;

        private LocalDateTime time;

        private String description;

        private Double latitude;

        private Double longitude;

        private Double accuracy;

        private Long evidenceId;

        private String fileName;

        private Integer durationSeconds;


        public TimelineEvent() {
        }


        public TimelineEvent(
                String type,
                LocalDateTime time,
                String description) {

            this.type = type;

            this.time = time;

            this.description = description;
        }


        // =================================================
        // TYPE
        // =================================================

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }


        // =================================================
        // TIME
        // =================================================

        public LocalDateTime getTime() {
            return time;
        }

        public void setTime(
                LocalDateTime time) {

            this.time = time;
        }


        // =================================================
        // DESCRIPTION
        // =================================================

        public String getDescription() {
            return description;
        }

        public void setDescription(
                String description) {

            this.description = description;
        }


        // =================================================
        // LATITUDE
        // =================================================

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(
                Double latitude) {

            this.latitude = latitude;
        }


        // =================================================
        // LONGITUDE
        // =================================================

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(
                Double longitude) {

            this.longitude = longitude;
        }


        // =================================================
        // ACCURACY
        // =================================================

        public Double getAccuracy() {
            return accuracy;
        }

        public void setAccuracy(
                Double accuracy) {

            this.accuracy = accuracy;
        }


        // =================================================
        // EVIDENCE ID
        // =================================================

        public Long getEvidenceId() {
            return evidenceId;
        }

        public void setEvidenceId(
                Long evidenceId) {

            this.evidenceId = evidenceId;
        }


        // =================================================
        // FILE NAME
        // =================================================

        public String getFileName() {
            return fileName;
        }

        public void setFileName(
                String fileName) {

            this.fileName = fileName;
        }


        // =================================================
        // DURATION
        // =================================================

        public Integer getDurationSeconds() {
            return durationSeconds;
        }

        public void setDurationSeconds(
                Integer durationSeconds) {

            this.durationSeconds =
                    durationSeconds;
        }
    }
}