package com.womensecurity.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "risk_zones")
public class RiskZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Double riskScore;

    @Column(nullable = false)
    private String riskLevel;

    private Integer crimeReports;

    private Integer userReports;

    private Boolean nightTime;

    private Boolean poorLighting;

    private Boolean lowCrowd;

    public RiskZone() {
    }

    public Long getId() {
        return id;
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

    public Double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Double riskScore) {
        this.riskScore = riskScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Integer getCrimeReports() {
        return crimeReports;
    }

    public void setCrimeReports(Integer crimeReports) {
        this.crimeReports = crimeReports;
    }

    public Integer getUserReports() {
        return userReports;
    }

    public void setUserReports(Integer userReports) {
        this.userReports = userReports;
    }

    public Boolean getNightTime() {
        return nightTime;
    }

    public void setNightTime(Boolean nightTime) {
        this.nightTime = nightTime;
    }

    public Boolean getPoorLighting() {
        return poorLighting;
    }

    public void setPoorLighting(Boolean poorLighting) {
        this.poorLighting = poorLighting;
    }

    public Boolean getLowCrowd() {
        return lowCrowd;
    }

    public void setLowCrowd(Boolean lowCrowd) {
        this.lowCrowd = lowCrowd;
    }
}