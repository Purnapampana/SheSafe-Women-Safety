package com.womensecurity.backend.dto;

public class SOSRequest {

    private Long userId;
    private Double latitude;
    private Double longitude;
    private String status;

    public SOSRequest() {
    }

    public SOSRequest(Long userId, Double latitude, Double longitude, String status) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}