package com.womensecurity.backend.dto;

import java.util.List;

public class RouteOption {

    private String routeName;

    private Double distanceKm;

    private Integer durationMinutes;

    private Double riskScore;

    private List<double[]> coordinates;

    private Double trafficScore;

    private String trafficLevel;

    private List<Double> riskPoints;

    /*
     * OSM safety / useful places shown as map icons
     *
     * Example:
     * STREETLIGHT
     * POLICE
     * HOSPITAL
     * SCHOOL
     * MARKET
     * BUS_STATION
     * PUBLIC_TRANSPORT
     * RESTAURANT
     * HOTEL
     */
    private List<MapFeature> mapFeatures;


    public RouteOption() {
    }


    public RouteOption(
            String routeName,
            Double distanceKm,
            Integer durationMinutes,
            Double riskScore,
            List<double[]> coordinates,
            Double trafficScore,
            String trafficLevel,
            List<Double> riskPoints,
            List<MapFeature> mapFeatures) {

        this.routeName = routeName;

        this.distanceKm = distanceKm;

        this.durationMinutes = durationMinutes;

        this.riskScore = riskScore;

        this.coordinates = coordinates;

        this.trafficScore = trafficScore;

        this.trafficLevel = trafficLevel;

        this.riskPoints = riskPoints;

        this.mapFeatures = mapFeatures;
    }


    public String getRouteName() {
        return routeName;
    }


    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }


    public Double getDistanceKm() {
        return distanceKm;
    }


    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }


    public Integer getDurationMinutes() {
        return durationMinutes;
    }


    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }


    public Double getRiskScore() {
        return riskScore;
    }


    public void setRiskScore(Double riskScore) {
        this.riskScore = riskScore;
    }


    public List<double[]> getCoordinates() {
        return coordinates;
    }


    public void setCoordinates(List<double[]> coordinates) {
        this.coordinates = coordinates;
    }


    public Double getTrafficScore() {
        return trafficScore;
    }


    public void setTrafficScore(Double trafficScore) {
        this.trafficScore = trafficScore;
    }


    public String getTrafficLevel() {
        return trafficLevel;
    }


    public void setTrafficLevel(String trafficLevel) {
        this.trafficLevel = trafficLevel;
    }


    public List<Double> getRiskPoints() {
        return riskPoints;
    }


    public void setRiskPoints(List<Double> riskPoints) {
        this.riskPoints = riskPoints;
    }


    public List<MapFeature> getMapFeatures() {
        return mapFeatures;
    }


    public void setMapFeatures(List<MapFeature> mapFeatures) {
        this.mapFeatures = mapFeatures;
    }
}