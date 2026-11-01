package com.womensecurity.backend.model;

import java.util.List;

public class WorkflowResponse {

    private String workflowStatus;
    private String sosStatus;
    private String threatLevel;
    private Integer dangerScore;
    private String nearestPoliceStation;
    private String nearestHospital;
    private String notificationStatus;
    private List<String> contacts;

    public WorkflowResponse() {
    }

    public String getWorkflowStatus() {
        return workflowStatus;
    }

    public void setWorkflowStatus(String workflowStatus) {
        this.workflowStatus = workflowStatus;
    }

    public String getSosStatus() {
        return sosStatus;
    }

    public void setSosStatus(String sosStatus) {
        this.sosStatus = sosStatus;
    }

    public String getThreatLevel() {
        return threatLevel;
    }

    public void setThreatLevel(String threatLevel) {
        this.threatLevel = threatLevel;
    }

    public Integer getDangerScore() {
        return dangerScore;
    }

    public void setDangerScore(Integer dangerScore) {
        this.dangerScore = dangerScore;
    }

    public String getNearestPoliceStation() {
        return nearestPoliceStation;
    }

    public void setNearestPoliceStation(String nearestPoliceStation) {
        this.nearestPoliceStation = nearestPoliceStation;
    }

    public String getNearestHospital() {
        return nearestHospital;
    }

    public void setNearestHospital(String nearestHospital) {
        this.nearestHospital = nearestHospital;
    }

    public String getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(String notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public List<String> getContacts() {
        return contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }

}