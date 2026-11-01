package com.womensecurity.backend.model;

public class ResponseMonitoringResponse {

    private long totalIncidents;
    private long resolved;
    private long active;
    private long pendingResponse;

    private Double averageResolutionMinutes;

    public ResponseMonitoringResponse() {
    }

    public long getTotalIncidents() {
        return totalIncidents;
    }

    public void setTotalIncidents(long totalIncidents) {
        this.totalIncidents = totalIncidents;
    }

    public long getResolved() {
        return resolved;
    }

    public void setResolved(long resolved) {
        this.resolved = resolved;
    }

    public long getActive() {
        return active;
    }

    public void setActive(long active) {
        this.active = active;
    }

    public long getPendingResponse() {
        return pendingResponse;
    }

    public void setPendingResponse(long pendingResponse) {
        this.pendingResponse = pendingResponse;
    }

    public Double getAverageResolutionMinutes() {
        return averageResolutionMinutes;
    }

    public void setAverageResolutionMinutes(
            Double averageResolutionMinutes) {
        this.averageResolutionMinutes =
                averageResolutionMinutes;
    }
}