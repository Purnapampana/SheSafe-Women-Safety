package com.womensecurity.backend.dto;

public class ThreatRequest {

    private Long userId;

    private Double latitude;

    private Double longitude;

    private String timeOfDay;

    // New movement fields
    private Double speed;

    private Double previousLatitude;

    private Double previousLongitude;


    public ThreatRequest() {
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


    public String getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(String timeOfDay) {
        this.timeOfDay = timeOfDay;
    }


    // Speed
    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }


    // Previous latitude
    public Double getPreviousLatitude() {
        return previousLatitude;
    }

    public void setPreviousLatitude(Double previousLatitude) {
        this.previousLatitude = previousLatitude;
    }


    // Previous longitude
    public Double getPreviousLongitude() {
        return previousLongitude;
    }

    public void setPreviousLongitude(Double previousLongitude) {
        this.previousLongitude = previousLongitude;
    }
}