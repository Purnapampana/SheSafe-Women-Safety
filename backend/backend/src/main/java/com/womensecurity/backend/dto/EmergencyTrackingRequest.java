package com.womensecurity.backend.dto;

public class EmergencyTrackingRequest {

    private Long userId;

    public EmergencyTrackingRequest() {
    }

    public EmergencyTrackingRequest(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}