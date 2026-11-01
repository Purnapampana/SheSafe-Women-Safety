package com.womensecurity.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "safe_routes")
public class SafeRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String startLocation;

    private String destination;

    private String recommendedRoute;

    private Double distance;

    private Integer estimatedTime;

    private String riskLevel;

    private LocalDateTime createdAt;

    public SafeRoute() {
    }

    public SafeRoute(Long id, Long userId, String startLocation,
                     String destination, String recommendedRoute,
                     Double distance, Integer estimatedTime,
                     String riskLevel, LocalDateTime createdAt) {

        this.id = id;
        this.userId = userId;
        this.startLocation = startLocation;
        this.destination = destination;
        this.recommendedRoute = recommendedRoute;
        this.distance = distance;
        this.estimatedTime = estimatedTime;
        this.riskLevel = riskLevel;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getRecommendedRoute() {
        return recommendedRoute;
    }

    public void setRecommendedRoute(String recommendedRoute) {
        this.recommendedRoute = recommendedRoute;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Integer getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(Integer estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}