package com.womensecurity.backend.model;

import java.util.List;

import com.womensecurity.backend.dto.RouteOption;

public class SafeRouteResponse {

    private String recommendedRoute;
    private String recommendation;
    private Double safestRiskScore;
    private Double startLatitude;
private Double startLongitude;
private Double destinationLatitude;
private Double destinationLongitude;

    private List<RouteOption> routes;

    public SafeRouteResponse() {
    }

    public SafeRouteResponse(
        String recommendedRoute,
        String recommendation,
        Double safestRiskScore,
        List<RouteOption> routes,
        Double startLatitude,
        Double startLongitude,
        Double destinationLatitude,
        Double destinationLongitude) {

    this.recommendedRoute =
            recommendedRoute;

    this.recommendation =
            recommendation;

    this.safestRiskScore =
            safestRiskScore;

    this.routes =
            routes;

    this.startLatitude =
            startLatitude;

    this.startLongitude =
            startLongitude;

    this.destinationLatitude =
            destinationLatitude;

    this.destinationLongitude =
            destinationLongitude;
}

    public String getRecommendedRoute() {
        return recommendedRoute;
    }

    public void setRecommendedRoute(
            String recommendedRoute) {

        this.recommendedRoute =
                recommendedRoute;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(
            String recommendation) {

        this.recommendation =
                recommendation;
    }

    public Double getSafestRiskScore() {
        return safestRiskScore;
    }

    public void setSafestRiskScore(
            Double safestRiskScore) {

        this.safestRiskScore =
                safestRiskScore;
    }

    public List<RouteOption> getRoutes() {
        return routes;
    }

    public void setRoutes(
            List<RouteOption> routes) {

        this.routes = routes;
    }
    public Double getStartLatitude() {

    return startLatitude;
}


public void setStartLatitude(
        Double startLatitude) {

    this.startLatitude =
            startLatitude;
}


public Double getStartLongitude() {

    return startLongitude;
}


public void setStartLongitude(
        Double startLongitude) {

    this.startLongitude =
            startLongitude;
}

public Double getDestinationLatitude() {
    return destinationLatitude;
}

public void setDestinationLatitude(Double destinationLatitude) {
    this.destinationLatitude = destinationLatitude;
}

public Double getDestinationLongitude() {
    return destinationLongitude;
}

public void setDestinationLongitude(Double destinationLongitude) {
    this.destinationLongitude = destinationLongitude;
}
}