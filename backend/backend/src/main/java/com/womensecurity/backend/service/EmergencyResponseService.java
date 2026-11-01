package com.womensecurity.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.womensecurity.backend.dto.EmergencyResponseRequest;
import com.womensecurity.backend.entity.Hospital;
import com.womensecurity.backend.entity.Location;
import com.womensecurity.backend.entity.PoliceStation;
import com.womensecurity.backend.entity.ThreatDetection;
import com.womensecurity.backend.model.EmergencyResponse;
import com.womensecurity.backend.repository.HospitalRepository;
import com.womensecurity.backend.repository.LocationRepository;
import com.womensecurity.backend.repository.PoliceStationRepository;
import com.womensecurity.backend.repository.ThreatRepository;
import com.womensecurity.backend.util.DistanceCalculator;

@Service
public class EmergencyResponseService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ThreatRepository threatRepository;

    @Autowired
    private PoliceStationRepository policeStationRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    public EmergencyResponse generateResponse(EmergencyResponseRequest request) {

        EmergencyResponse response = new EmergencyResponse();

        // -------------------------
        // Get Latest User Location
        // -------------------------
        Optional<Location> locationOptional =
                locationRepository.findFirstByUserIdOrderByUpdatedAtDesc(
                        request.getUserId());

        if (locationOptional.isEmpty()) {

            response.setRecommendedAction(
                    "User location not found.");

            return response;
        }

        Location userLocation = locationOptional.get();

        double userLatitude = userLocation.getLatitude();
        double userLongitude = userLocation.getLongitude();

        // -------------------------
        // Get Latest Threat
        // -------------------------
        // -------------------------
// Get Latest Threat
// -------------------------

List<ThreatDetection> threats =
        threatRepository.findByUserId(request.getUserId());

if (!threats.isEmpty()) {

    ThreatDetection threat = threats.get(threats.size() - 1);

    response.setThreatLevel(
            threat.getRiskLevel());

    response.setDangerScore(
            threat.getDangerScore());

} else {

    response.setThreatLevel("UNKNOWN");
    response.setDangerScore(0);

}

        // -------------------------
        // Find Nearest Police Station
        // -------------------------

        List<PoliceStation> policeStations =
                policeStationRepository.findAll();

        double nearestPoliceDistance = Double.MAX_VALUE;

        PoliceStation nearestPolice = null;

        for (PoliceStation station : policeStations) {

            double distance =
                    DistanceCalculator.calculate(
                            userLatitude,
                            userLongitude,
                            station.getLatitude(),
                            station.getLongitude());

            if (distance < nearestPoliceDistance) {

                nearestPoliceDistance = distance;
                nearestPolice = station;

            }

        }

        if (nearestPolice != null) {

            response.setNearestPoliceStation(
                    nearestPolice.getStationName());

            response.setPolicePhone(
                    nearestPolice.getPhoneNumber());

        }

        // -------------------------
        // Find Nearest Hospital
        // -------------------------

        List<Hospital> hospitals =
                hospitalRepository.findAll();

        double nearestHospitalDistance = Double.MAX_VALUE;

        Hospital nearestHospital = null;

        for (Hospital hospital : hospitals) {

            double distance =
                    DistanceCalculator.calculate(
                            userLatitude,
                            userLongitude,
                            hospital.getLatitude(),
                            hospital.getLongitude());

            if (distance < nearestHospitalDistance) {

                nearestHospitalDistance = distance;
                nearestHospital = hospital;

            }

        }

        if (nearestHospital != null) {

            response.setNearestHospital(
                    nearestHospital.getHospitalName());

            response.setHospitalPhone(
                    nearestHospital.getPhoneNumber());

        }

        // -------------------------
        // Generate Recommendation
        // -------------------------

        String threatLevel = response.getThreatLevel();

        if ("HIGH".equalsIgnoreCase(threatLevel)) {

            response.setRecommendedAction(
                    "HIGH RISK! Activate SOS immediately. "
                    + "Proceed to "
                    + response.getNearestPoliceStation()
                    + ". If injured, visit "
                    + response.getNearestHospital() + ".");

        }

        else if ("MEDIUM".equalsIgnoreCase(threatLevel)) {

            response.setRecommendedAction(
                    "Stay alert. Share your live location "
                    + "with emergency contacts. "
                    + "Nearest Police: "
                    + response.getNearestPoliceStation());

        }

        else {

            response.setRecommendedAction(
                    "Area appears safe. Stay aware "
                    + "and continue your journey.");

        }

        return response;

    }

}