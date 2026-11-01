package com.womensecurity.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "incident_timeline")
public class IncidentTimeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =====================================================
    // USER
    // =====================================================

    @Column(nullable = false)
    private Long userId;


    // =====================================================
    // USER-SPECIFIC INCIDENT NUMBER
    // =====================================================

    @Column(nullable = false)
    private Long incidentNumber;


    // =====================================================
    // SOS TIME
    // =====================================================

    @Column(nullable = false)
    private LocalDateTime sosActivationTime;


    // =====================================================
    // RESPONSE STATUS
    // =====================================================

    private String emergencyResponseStatus;


    // =====================================================
    // RESOLVED TIME
    // =====================================================

    private LocalDateTime resolvedAt;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public IncidentTimeline() {
    }


    public IncidentTimeline(
            Long id,
            Long userId,
            Long incidentNumber,
            LocalDateTime sosActivationTime,
            String emergencyResponseStatus,
            LocalDateTime resolvedAt) {

        this.id = id;

        this.userId = userId;

        this.incidentNumber = incidentNumber;

        this.sosActivationTime =
                sosActivationTime;

        this.emergencyResponseStatus =
                emergencyResponseStatus;

        this.resolvedAt =
                resolvedAt;
    }


    // =====================================================
    // ID
    // =====================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    // =====================================================
    // USER ID
    // =====================================================

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    // =====================================================
    // INCIDENT NUMBER
    // =====================================================

    public Long getIncidentNumber() {
        return incidentNumber;
    }

    public void setIncidentNumber(
            Long incidentNumber) {

        this.incidentNumber =
                incidentNumber;
    }


    // =====================================================
    // SOS ACTIVATION TIME
    // =====================================================

    public LocalDateTime getSosActivationTime() {
        return sosActivationTime;
    }

    public void setSosActivationTime(
            LocalDateTime sosActivationTime) {

        this.sosActivationTime =
                sosActivationTime;
    }


    // =====================================================
    // RESPONSE STATUS
    // =====================================================

    public String getEmergencyResponseStatus() {
        return emergencyResponseStatus;
    }

    public void setEmergencyResponseStatus(
            String emergencyResponseStatus) {

        this.emergencyResponseStatus =
                emergencyResponseStatus;
    }


    // =====================================================
    // RESOLVED AT
    // =====================================================

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(
            LocalDateTime resolvedAt) {

        this.resolvedAt =
                resolvedAt;
    }
}