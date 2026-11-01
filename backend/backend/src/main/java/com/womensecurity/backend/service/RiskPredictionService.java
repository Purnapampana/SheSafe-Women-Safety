package com.womensecurity.backend.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.womensecurity.backend.dto.RiskPredictionRequest;
import com.womensecurity.backend.entity.IncidentReport;
import com.womensecurity.backend.entity.RiskPredictionLog;
import com.womensecurity.backend.model.RiskPredictionResponse;
import com.womensecurity.backend.repository.IncidentReportRepository;
import com.womensecurity.backend.repository.RiskPredictionRepository;

@Service
public class RiskPredictionService {

    private final IncidentReportRepository incidentReportRepository;

    private final RiskPredictionRepository riskPredictionLogRepository;
    private final WeatherService weatherService;
    private final PopulationDensityService populationDensityService;
    private final StreetLightingService streetLightingService;
    private final TrafficAnalysisService trafficAnalysisService;

    
    public RiskPredictionService(
            IncidentReportRepository incidentReportRepository,
            RiskPredictionRepository riskPredictionLogRepository,WeatherService weatherService,
            PopulationDensityService populationDensityService,
            StreetLightingService streetLightingService,
            TrafficAnalysisService trafficAnalysisService) {

        this.incidentReportRepository =
                incidentReportRepository;

        this.riskPredictionLogRepository =
                riskPredictionLogRepository;
                this.weatherService =
        weatherService;
        this.populationDensityService =
                populationDensityService;
        this.streetLightingService =
                streetLightingService;
        this.trafficAnalysisService =
                trafficAnalysisService;
    }


    public RiskPredictionResponse predictRisk(
            RiskPredictionRequest request) {

        Double latitude = request.getLatitude();
        Double longitude = request.getLongitude();


        /*
         * 1. HISTORICAL CRIME
         *
         * Currently calculated from existing
         * crowd-sourced incident reports near
         * the user's location.
         */
        double historicalCrimeScore =
                calculateHistoricalCrimeScore(
                        latitude,
                        longitude
                );


        /*
         * 2. USER REPORTS
         */
        double userReportScore =
                calculateUserReportScore(
                        latitude,
                        longitude
                );


        /*
         * 3. TIME OF DAY
         */
        double timeOfDayScore =
                calculateTimeOfDayScore();


        /*
         * 4. POPULATION DENSITY
         *
         * Temporary baseline.
         * Will be replaced with real
         * geographic population data.
         */
       double populationDensityScore =
        populationDensityService
                .getPopulationDensityRisk(
                        latitude,
                        longitude
                );


        /*
         * 5. STREET LIGHTING
         *
         * Temporary baseline.
         * Will later use real lighting data.
         */
       Double lightingScore =
        streetLightingService.getLightingRisk(
                latitude,
                longitude
        );


        /*
         * 6. WEATHER
         *
         * Temporary baseline.
         * Will later use a weather API.
         */
        double weatherScore =
        weatherService.getWeatherRisk(
                latitude,
                longitude
        );
        Double trafficScore =
        trafficAnalysisService.getTrafficRisk(
                request.getLatitude(),
                request.getLongitude()
        );


        /*
         * WEIGHTED RISK SCORE
         *
         * Historical Crime  = 30%
         * User Reports      = 20%
         * Time of Day       = 15%
         * Population        = 10%
         * Lighting          = 10%
         * Weather           = 5%
         *
         * Remaining 10% is currently
         * represented by baseline safety factors.
         */

// =====================================================
// WEIGHTED RISK SCORE
// =====================================================

double weightedScore = 0.0;
double totalWeight = 0.0;


// Historical Crime = 30%
weightedScore +=
        historicalCrimeScore * 0.30;

totalWeight += 0.30;


// User Reports = 20%
weightedScore +=
        userReportScore * 0.20;

totalWeight += 0.20;


// Time of Day = 15%
weightedScore +=
        timeOfDayScore * 0.15;

totalWeight += 0.15;


// Population Density = 10%
weightedScore +=
        populationDensityScore * 0.10;

totalWeight += 0.10;


// Street Lighting = 10%
if (lightingScore != null) {

    weightedScore +=
            lightingScore * 0.10;

    totalWeight += 0.10;

} else {

    System.out.println(
            "LIGHTING SCORE UNAVAILABLE - "
            + "EXCLUDING LIGHTING FROM RISK CALCULATION"
    );
}


// Weather = 5%
weightedScore +=
        weatherScore * 0.05;

totalWeight += 0.05;


// Traffic = 10%
if (trafficScore != null) {

    weightedScore +=
            trafficScore * 0.10;

    totalWeight += 0.10;

} else {

    System.out.println(
            "TRAFFIC SCORE UNAVAILABLE - "
            + "EXCLUDING TRAFFIC FROM RISK CALCULATION"
    );
}


// =====================================================
// NORMALIZE SCORE
// =====================================================

double riskScore = 0.0;

if (totalWeight > 0) {

    riskScore =
            weightedScore / totalWeight;
}


// Keep score between 0 and 100

if (riskScore > 100) {
    riskScore = 100;
}

if (riskScore < 0) {
    riskScore = 0;
}


        String riskLevel =
                determineRiskLevel(riskScore);


        String recommendation =
                generateRecommendation(riskLevel);


        /*
         * Save prediction history.
         */
        RiskPredictionLog log =
                new RiskPredictionLog();

        log.setRiskScore(
                (int) Math.round(riskScore)
        );

        log.setRiskLevel(
                riskLevel
        );

        log.setRecommendation(
                recommendation
        );

        log.setCreatedAt(
                LocalDateTime.now()
        );

        riskPredictionLogRepository.save(log);


        /*
         * Prepare response.
         */
        RiskPredictionResponse response =
                new RiskPredictionResponse();

        response.setLatitude(latitude);
        response.setLongitude(longitude);

        response.setRiskScore(
                Math.round(riskScore * 100.0) / 100.0
        );

        response.setRiskLevel(
                riskLevel
        );

        response.setRecommendation(
                recommendation
        );

        response.setHistoricalCrimeScore(
                historicalCrimeScore
        );

        response.setUserReportScore(
                userReportScore
        );

        response.setTimeOfDayScore(
                timeOfDayScore
        );

        response.setPopulationDensityScore(
                populationDensityScore
        );

        response.setLightingScore(
                lightingScore
        );

        response.setWeatherScore(
                weatherScore
        );
        response.setTrafficScore(
        trafficScore
);

        return response;
    }


 private double calculateHistoricalCrimeScore(
        Double latitude,
        Double longitude) {

    List<IncidentReport> reports =
            incidentReportRepository.findAll();

    LocalDateTime thirtyDaysAgo =
            LocalDateTime.now().minusDays(30);

    double score = 0;

    for (IncidentReport report : reports) {

        if (report.getLatitude() == null ||
                report.getLongitude() == null ||
                report.getReportedAt() == null) {
            continue;
        }

        // Only older reports are historical crime
        if (report.getReportedAt()
                .isAfter(thirtyDaysAgo)) {
            continue;
        }

        double distance =
                calculateDistance(
                        latitude,
                        longitude,
                        report.getLatitude(),
                        report.getLongitude()
                );

        if (distance > 2.0) {
            continue;
        }

        double severity =
                getIncidentSeverity(
                        report.getIncidentType()
                );

        double distanceWeight =
                1.0 - (distance / 2.0);

        score +=
                severity * distanceWeight;
    }

    return Math.min(score, 100.0);
}


private double calculateUserReportScore(
        Double latitude,
        Double longitude) {

    List<IncidentReport> reports =
            incidentReportRepository.findAll();

    LocalDateTime thirtyDaysAgo =
            LocalDateTime.now().minusDays(30);

    double score = 0;

    for (IncidentReport report : reports) {

        if (report.getLatitude() == null ||
                report.getLongitude() == null ||
                report.getReportedAt() == null) {
            continue;
        }

        // Only recent reports are user reports
        if (report.getReportedAt()
                .isBefore(thirtyDaysAgo)) {
            continue;
        }

        double distance =
                calculateDistance(
                        latitude,
                        longitude,
                        report.getLatitude(),
                        report.getLongitude()
                );

        if (distance > 1.0) {
            continue;
        }

        double severity =
                getIncidentSeverity(
                        report.getIncidentType()
                );

        double distanceWeight =
                1.0 - (distance / 1.0);

        score +=
                severity * distanceWeight;
    }

    return Math.min(score, 100.0);
}
private double getIncidentSeverity(
        String incidentType) {

    if (incidentType == null) {
        return 10.0;
    }

    switch (incidentType.toUpperCase()) {

        case "ASSAULT":
            return 35.0;

        case "STALKING":
            return 30.0;

        case "HARASSMENT":
            return 25.0;

        case "THEFT":
            return 20.0;

        case "SUSPICIOUS_PERSON":
            return 15.0;

        case "UNSAFE_LOCATION":
            return 10.0;

        default:
            return 10.0;
    }
}


    private double calculateTimeOfDayScore() {

        LocalTime currentTime =
                LocalTime.now();

        int hour =
                currentTime.getHour();


        /*
         * Late night / early morning
         */
        if (hour >= 22 || hour < 5) {
            return 90.0;
        }


        /*
         * Evening
         */
        if (hour >= 19) {
            return 60.0;
        }


        /*
         * Early morning
         */
        if (hour < 7) {
            return 50.0;
        }


        /*
         * Daytime
         */
        return 15.0;
    }


    private String determineRiskLevel(
            double score) {

        if (score >= 70) {
            return "CRIME HOTSPOT";
        }

        if (score >= 50) {
            return "HIGH RISK";
        }

        if (score >= 25) {
            return "MODERATE RISK";
        }

        return "SAFE ZONE";
    }


    private String generateRecommendation(
            String riskLevel) {

        switch (riskLevel) {

            case "CRIME HOTSPOT":
                return "Avoid this area if possible. Consider taking a safer alternative route.";

            case "HIGH RISK":
                return "Stay alert and prefer well-lit and crowded areas.";

            case "MODERATE RISK":
                return "Exercise caution and avoid isolated locations.";

            default:
                return "This location currently shows relatively low risk.";
        }
    }


    /*
     * Calculates approximate distance between
     * two GPS coordinates in kilometers.
     */
    private double calculateDistance(
            double lat1,
            double lon1,
            double lat2,
            double lon2) {

        final double EARTH_RADIUS = 6371.0;

        double dLat =
                Math.toRadians(lat2 - lat1);

        double dLon =
                Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(dLat / 2)
                * Math.sin(dLat / 2)
                + Math.cos(
                        Math.toRadians(lat1)
                  )
                * Math.cos(
                        Math.toRadians(lat2)
                  )
                * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);

        double c =
                2 * Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1 - a)
                );

        return EARTH_RADIUS * c;
    }
}