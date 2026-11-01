package com.womensecurity.backend.dto;

public class NotificationRequest {

    private Long userId;

    public NotificationRequest() {
    }

    public NotificationRequest(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}