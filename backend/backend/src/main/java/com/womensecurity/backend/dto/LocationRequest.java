package com.womensecurity.backend.dto;

public class LocationRequest {

    private Long userId;
    private Double latitude;
    private Double longitude;
    private Long incidentId;
private Double accuracy;

    public LocationRequest() {
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
    public Long getIncidentId() {
    return incidentId;
}

public void setIncidentId(Long incidentId) {
    this.incidentId = incidentId;
}

public Double getAccuracy() {
    return accuracy;
}

public void setAccuracy(Double accuracy) {
    this.accuracy = accuracy;
}
}