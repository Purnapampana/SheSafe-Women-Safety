package com.womensecurity.backend.dto;

public class EmergencyResponseRequest {

    private Long userId;

    public EmergencyResponseRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}