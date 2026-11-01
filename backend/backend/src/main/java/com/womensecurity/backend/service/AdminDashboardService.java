package com.womensecurity.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.womensecurity.backend.dto.AdminDashboardResponse;
import com.womensecurity.backend.repository.EmergencyContactRepository;
import com.womensecurity.backend.repository.HospitalRepository;
import com.womensecurity.backend.repository.PoliceStationRepository;
import com.womensecurity.backend.repository.UserRepository;
import com.womensecurity.backend.repository.SOSRepository;
import com.womensecurity.backend.repository.ThreatRepository;
import com.womensecurity.backend.repository.CrimeHotspotRepository;
import com.womensecurity.backend.repository.RiskPredictionRepository;

@Service
public class AdminDashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SOSRepository sosRepository;

    @Autowired
    private ThreatRepository threatRepository;

    @Autowired
    private CrimeHotspotRepository crimeHotspotRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private PoliceStationRepository policeStationRepository;

    @Autowired
    private EmergencyContactRepository emergencyContactRepository;

    @Autowired
    private RiskPredictionRepository riskPredictionRepository;


    // ==========================================
    // ADMIN DASHBOARD
    // ==========================================

    public AdminDashboardResponse getDashboard() {

        AdminDashboardResponse response =
                new AdminDashboardResponse();


        // ======================================
        // TOTAL USERS
        // ======================================

        response.setTotalUsers(
                userRepository.count());


        // ======================================
        // TOTAL SOS
        // ======================================

        response.setTotalSOS(
                sosRepository.count());


        // ======================================
        // ACTIVE EMERGENCIES
        // ======================================

        long activeEmergencies =
                sosRepository
                        .countByStatus("ACTIVE");

        response.setActiveEmergencies(
                activeEmergencies);


        // ======================================
        // RESOLVED EMERGENCIES
        // ======================================

        long resolvedEmergencies =
                sosRepository
                        .countByStatus("RESOLVED");

        response.setResolvedEmergencies(
                resolvedEmergencies);


        // ======================================
        // TOTAL THREATS
        // ======================================

        response.setTotalThreats(
                threatRepository.count());


        // ======================================
        // TOTAL RISK PREDICTIONS
        // ======================================

        response.setTotalRiskPredictions(
                riskPredictionRepository.count());


        // ======================================
        // TOTAL CRIME HOTSPOTS
        // ======================================

        response.setTotalCrimeHotspots(
                crimeHotspotRepository.count());


        // ======================================
        // TOTAL HOSPITALS
        // ======================================

        response.setTotalHospitals(
                hospitalRepository.count());


        // ======================================
        // TOTAL POLICE STATIONS
        // ======================================

        response.setTotalPoliceStations(
                policeStationRepository.count());


        // ======================================
        // TOTAL EMERGENCY CONTACTS
        // ======================================

        response.setTotalEmergencyContacts(
                emergencyContactRepository.count());


        // ======================================
        // APPLICATION HEALTH
        // ======================================

        response.setApplicationStatus(
                "HEALTHY");


        return response;
    }
}