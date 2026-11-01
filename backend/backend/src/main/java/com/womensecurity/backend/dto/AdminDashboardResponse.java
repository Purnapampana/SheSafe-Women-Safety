package com.womensecurity.backend.dto;

public class AdminDashboardResponse {

    // ================= USER STATISTICS =================

    private long totalUsers;

    // ================= SOS STATISTICS =================

    private long totalSOS;

    private long activeEmergencies;

    private long resolvedEmergencies;

    // ================= THREAT STATISTICS =================

    private long totalThreats;

    // ================= RISK PREDICTION STATISTICS =================

    private long totalRiskPredictions;

    // ================= CRIME HOTSPOT STATISTICS =================

    private long totalCrimeHotspots;

    // ================= EMERGENCY SERVICES =================

    private long totalHospitals;

    private long totalPoliceStations;

    // ================= EMERGENCY CONTACTS =================

    private long totalEmergencyContacts;

    // ================= APPLICATION HEALTH =================

    private String applicationStatus;

    public AdminDashboardResponse() {
    }

    // ================= GETTERS AND SETTERS =================

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalSOS() {
        return totalSOS;
    }

    public void setTotalSOS(long totalSOS) {
        this.totalSOS = totalSOS;
    }

    public long getActiveEmergencies() {
        return activeEmergencies;
    }

    public void setActiveEmergencies(long activeEmergencies) {
        this.activeEmergencies = activeEmergencies;
    }

    public long getResolvedEmergencies() {
        return resolvedEmergencies;
    }

    public void setResolvedEmergencies(
            long resolvedEmergencies) {

        this.resolvedEmergencies =
                resolvedEmergencies;
    }

    public long getTotalThreats() {
        return totalThreats;
    }

    public void setTotalThreats(long totalThreats) {
        this.totalThreats = totalThreats;
    }

    public long getTotalRiskPredictions() {
        return totalRiskPredictions;
    }

    public void setTotalRiskPredictions(
            long totalRiskPredictions) {

        this.totalRiskPredictions =
                totalRiskPredictions;
    }

    public long getTotalCrimeHotspots() {
        return totalCrimeHotspots;
    }

    public void setTotalCrimeHotspots(
            long totalCrimeHotspots) {

        this.totalCrimeHotspots =
                totalCrimeHotspots;
    }

    public long getTotalHospitals() {
        return totalHospitals;
    }

    public void setTotalHospitals(long totalHospitals) {
        this.totalHospitals = totalHospitals;
    }

    public long getTotalPoliceStations() {
        return totalPoliceStations;
    }

    public void setTotalPoliceStations(
            long totalPoliceStations) {

        this.totalPoliceStations =
                totalPoliceStations;
    }

    public long getTotalEmergencyContacts() {
        return totalEmergencyContacts;
    }

    public void setTotalEmergencyContacts(
            long totalEmergencyContacts) {

        this.totalEmergencyContacts =
                totalEmergencyContacts;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(
            String applicationStatus) {

        this.applicationStatus =
                applicationStatus;
    }
}