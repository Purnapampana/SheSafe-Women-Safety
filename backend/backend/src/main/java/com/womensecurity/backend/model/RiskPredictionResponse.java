package com.womensecurity.backend.model;

public class RiskPredictionResponse {

    private Double latitude;
    private Double longitude;

    private Double riskScore;
    private String riskLevel;
    private String recommendation;
    private Double trafficScore;

    private Double historicalCrimeScore;
    private Double userReportScore;
    private Double timeOfDayScore;
    private Double populationDensityScore;
    private Double lightingScore;
    private Double weatherScore;


    public RiskPredictionResponse() {
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


    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(
            String recommendation) {

        this.recommendation =
                recommendation;
    }


    public Double getHistoricalCrimeScore() {
        return historicalCrimeScore;
    }

    public void setHistoricalCrimeScore(
            Double historicalCrimeScore) {

        this.historicalCrimeScore =
                historicalCrimeScore;
    }


    public Double getUserReportScore() {
        return userReportScore;
    }

    public void setUserReportScore(
            Double userReportScore) {

        this.userReportScore =
                userReportScore;
    }


    public Double getTimeOfDayScore() {
        return timeOfDayScore;
    }

    public void setTimeOfDayScore(
            Double timeOfDayScore) {

        this.timeOfDayScore =
                timeOfDayScore;
    }


    public Double getPopulationDensityScore() {
        return populationDensityScore;
    }

    public void setPopulationDensityScore(
            Double populationDensityScore) {

        this.populationDensityScore =
                populationDensityScore;
    }


    public Double getLightingScore() {
        return lightingScore;
    }

    public void setLightingScore(
            Double lightingScore) {

        this.lightingScore =
                lightingScore;
    }


    public Double getWeatherScore() {
        return weatherScore;
    }

    public void setWeatherScore(
            Double weatherScore) {

        this.weatherScore =
                weatherScore;
    }
    public Double getTrafficScore() {
    return trafficScore;
}

public void setTrafficScore(
        Double trafficScore) {

    this.trafficScore =
            trafficScore;
}
}