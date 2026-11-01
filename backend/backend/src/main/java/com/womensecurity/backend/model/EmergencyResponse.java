package com.womensecurity.backend.model;

public class EmergencyResponse {

    private String threatLevel;
    private Integer dangerScore;

    private String nearestPoliceStation;
    private String policePhone;

    private String nearestHospital;
    private String hospitalPhone;

    private String recommendedAction;

    public EmergencyResponse() {
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

    public String getPolicePhone() {
        return policePhone;
    }

    public void setPolicePhone(String policePhone) {
        this.policePhone = policePhone;
    }

    public String getNearestHospital() {
        return nearestHospital;
    }

    public void setNearestHospital(String nearestHospital) {
        this.nearestHospital = nearestHospital;
    }

    public String getHospitalPhone() {
        return hospitalPhone;
    }

    public void setHospitalPhone(String hospitalPhone) {
        this.hospitalPhone = hospitalPhone;
    }

    public String getRecommendedAction() {
        return recommendedAction;
    }

    public void setRecommendedAction(String recommendedAction) {
        this.recommendedAction = recommendedAction;
    }
}