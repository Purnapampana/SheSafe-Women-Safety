package com.womensecurity.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.womensecurity.backend.dto.MapFeature;
import com.womensecurity.backend.dto.RouteOption;
import com.womensecurity.backend.dto.SafeRouteRequest;
import com.womensecurity.backend.model.SafeRouteResponse;

@Service
public class SafeRouteService {

    private final GeocodingService geocodingService;

    private final RestTemplate restTemplate;

    @Value("${safe-route.python-url:http://localhost:5000}")
    private String pythonSafeRouteUrl;


    public SafeRouteService(
            GeocodingService geocodingService) {

        this.geocodingService = geocodingService;

        this.restTemplate = new RestTemplate();
    }


    // =====================================================
    // RECOMMEND SAFE ROUTE
    // =====================================================

    public SafeRouteResponse recommendRoute(
            SafeRouteRequest request) {

        // =================================================
        // STARTING LOCATION
        // =================================================

        Double startLatitude =
                request.getStartLatitude();

        Double startLongitude =
                request.getStartLongitude();


        // =================================================
        // GEOCODE START ADDRESS
        // =================================================

        if ((startLatitude == null ||
                startLongitude == null)
                &&
                request.getStartAddress() != null
                &&
                !request.getStartAddress()
                        .trim()
                        .isEmpty()) {

            double[] coordinates =
                    geocodingService.geocodeAddress(
                            request.getStartAddress()
                    );

            if (coordinates == null) {

                throw new RuntimeException(
                        "Starting address could not be found."
                );
            }

            startLatitude =
                    coordinates[0];

            startLongitude =
                    coordinates[1];
        }


        if (startLatitude == null ||
                startLongitude == null) {

            throw new RuntimeException(
                    "Starting location is required."
            );
        }


        // =================================================
        // DESTINATION
        // =================================================

        Double destinationLatitude =
                request.getDestinationLatitude();

        Double destinationLongitude =
                request.getDestinationLongitude();


        // =================================================
        // GEOCODE DESTINATION ADDRESS
        // =================================================

        if ((destinationLatitude == null ||
                destinationLongitude == null)
                &&
                request.getDestinationAddress() != null
                &&
                !request.getDestinationAddress()
                        .trim()
                        .isEmpty()) {

            double[] coordinates =
                    geocodingService.geocodeAddress(
                            request.getDestinationAddress()
                    );

            if (coordinates == null) {

                throw new RuntimeException(
                        "Destination address could not be found."
                );
            }

            destinationLatitude =
                    coordinates[0];

            destinationLongitude =
                    coordinates[1];
        }


        if (destinationLatitude == null ||
                destinationLongitude == null) {

            throw new RuntimeException(
                    "Destination is required."
            );
        }


        // =================================================
        // CALL PYTHON SAFE ROUTE ENGINE
        // =================================================

        Map<String, Object> pythonRequest =
                Map.of(
                        "startLatitude",
                        startLatitude,

                        "startLongitude",
                        startLongitude,

                        "destinationLatitude",
                        destinationLatitude,

                        "destinationLongitude",
                        destinationLongitude
                );


        System.out.println();
        System.out.println(
                "==================================="
        );

        System.out.println(
                "CALLING PYTHON SAFE ROUTE ENGINE"
        );

        System.out.println(
                "==================================="
        );

        System.out.println(
                "Python URL: "
                        + pythonSafeRouteUrl
        );


        Map<String, Object> pythonResponse =
                restTemplate.postForObject(
                        pythonSafeRouteUrl
                                + "/safe-route",
                        pythonRequest,
                        Map.class
                );


        if (pythonResponse == null) {

            throw new RuntimeException(
                    "Python safe route service returned no response."
            );
        }


        // =================================================
        // CHECK PYTHON RESPONSE
        // =================================================

        Object responseStatus =
                pythonResponse.get("status");

        if (responseStatus != null &&
                !"success".equalsIgnoreCase(
                        responseStatus.toString()
                )) {

            Object message =
                    pythonResponse.get("message");

            throw new RuntimeException(
                    message != null
                            ? message.toString()
                            : "Python safe route calculation failed."
            );
        }


        // =================================================
        // READ PYTHON ROUTES
        // =================================================

        Map<String, Object> safestRouteData =
                getMap(
                        pythonResponse.get(
                                "safestRoute"
                        )
                );


        Map<String, Object> alternativeRouteData =
                getMap(
                        pythonResponse.get(
                                "alternativeRoute"
                        )
                );


        if (safestRouteData.isEmpty()) {

            throw new RuntimeException(
                    "Python service did not return a safest route."
            );
        }


        // =================================================
        // ROUTE LIST
        // =================================================

        List<RouteOption> routes =
                new ArrayList<>();


        // =================================================
        // ADD PYTHON SAFEST ROUTE
        // =================================================

        RouteOption pythonSafestRoute =
                convertPythonRoute(
                        safestRouteData,
                        "Safest Route"
                );

        routes.add(
                pythonSafestRoute
        );


        // =================================================
        // ADD ALTERNATIVE ROUTE
        // =================================================

        if (!alternativeRouteData.isEmpty() &&
                hasCoordinates(
                        alternativeRouteData
                )) {

            RouteOption alternativeRoute =
                    convertPythonRoute(
                            alternativeRouteData,
                            "Alternative Route"
                    );

            routes.add(
                    alternativeRoute
            );
        }


        // =================================================
        // FIND BEST ROUTE
        // =================================================
        //
        // Priority:
        //
        // 1. Lowest AI risk
        // 2. Lowest traffic
        // 3. Shortest duration
        // 4. Shortest distance
        //
        // =================================================

        RouteOption safestRoute =
                routes.get(0);


        for (RouteOption route : routes) {

            double currentRisk =
                    route.getRiskScore();

            double bestRisk =
                    safestRoute.getRiskScore();


            // =============================================
            // 1. LOWER AI RISK
            // =============================================

            if (currentRisk < bestRisk) {

                safestRoute = route;

                continue;
            }


            // =============================================
            // ONLY CONTINUE IF RISK IS SAME
            // =============================================

            if (Double.compare(
                    currentRisk,
                    bestRisk
            ) != 0) {

                continue;
            }


            // =============================================
            // 2. LOWER TRAFFIC RISK
            // =============================================

            Double currentTraffic =
                    route.getTrafficScore();

            Double bestTraffic =
                    safestRoute.getTrafficScore();


            if (currentTraffic != null &&
                    bestTraffic != null &&
                    currentTraffic < bestTraffic) {

                safestRoute = route;

                continue;
            }


            // =============================================
            // 3. SHORTER DURATION
            // =============================================

            Integer currentDuration =
                    route.getDurationMinutes();

            Integer bestDuration =
                    safestRoute.getDurationMinutes();


            if (currentDuration != null &&
                    bestDuration != null &&
                    currentDuration < bestDuration) {

                safestRoute = route;

                continue;
            }


            // =============================================
            // 4. SHORTER DISTANCE
            // =============================================

            Double currentDistance =
                    route.getDistanceKm();

            Double bestDistance =
                    safestRoute.getDistanceKm();


            if (currentDistance != null &&
                    bestDistance != null &&
                    currentDistance < bestDistance) {

                safestRoute = route;
            }
        }


        // =================================================
        // SELECTED AI RISK
        // =================================================

        double safestRiskScore =
                safestRoute.getRiskScore();


        // =================================================
        // RECOMMENDATION
        // =================================================

        String recommendation =
                "Prefer "
                        + safestRoute.getRouteName()
                        + " because it provides the best "
                        + "combination of AI safety risk, "
                        + "traffic conditions, travel time "
                        + "and distance.";


        // =================================================
        // RESPONSE
        // =================================================

        return new SafeRouteResponse(

                safestRoute.getRouteName(),

                recommendation,

                safestRiskScore,

                routes,

                startLatitude,

                startLongitude,

                destinationLatitude,

                destinationLongitude
        );
    }


    // =====================================================
    // CONVERT PYTHON ROUTE
    // =====================================================

    private RouteOption convertPythonRoute(
            Map<String, Object> routeData,
            String defaultRouteName) {

        // =================================================
        // ROUTE NAME
        // =================================================

        String routeName =
                getString(
                        routeData,
                        "routeName",
                        defaultRouteName
                );


        // =================================================
        // DISTANCE
        // =================================================

        Double distanceKm =
                getDouble(
                        routeData,
                        "distanceKm"
                );


        // =================================================
        // DURATION
        // =================================================

        Integer durationMinutes =
                getInteger(
                        routeData,
                        "durationMinutes"
                );


        // =================================================
        // AI RISK
        // =================================================

        Double riskScore =
                getDouble(
                        routeData,
                        "riskScore"
                );


        if (riskScore == null) {

            riskScore = 0.0;
        }


        // =================================================
        // TRAFFIC SCORE
        // =================================================

        Double trafficScore =
                getDouble(
                        routeData,
                        "trafficScore"
                );


        // =================================================
        // TRAFFIC LEVEL
        // =================================================

        String trafficLevel =
                getString(
                        routeData,
                        "trafficLevel",
                        getTrafficLevel(trafficScore)
                );


        // =================================================
        // ROUTE COORDINATES
        // =================================================

        List<double[]> coordinates =
                convertCoordinates(
                        routeData.get(
                                "coordinates"
                        )
                );


        // =================================================
        // RISK POINTS
        // =================================================

        List<Double> riskPoints =
                convertRiskPoints(
                        routeData.get(
                                "riskPoints"
                        )
                );


        // =================================================
        // OSM MAP FEATURES
        // =================================================
        //
        // Hotels, restaurants, hospitals,
        // schools, police, street lights,
        // bus stations, markets, etc.
        //
        // These are displayed as icons on
        // the map instead of numbers.
        //
        // =================================================

        List<MapFeature> mapFeatures =
                convertMapFeatures(
                        routeData.get(
                                "mapFeatures"
                        )
                );


        // =================================================
        // CREATE ROUTE OPTION
        // =================================================

        return new RouteOption(

                routeName,

                distanceKm != null
                        ? distanceKm
                        : 0.0,

                durationMinutes != null
                        ? durationMinutes
                        : 0,

                riskScore,

                coordinates,

                trafficScore,

                trafficLevel,

                riskPoints,

                mapFeatures
        );
    }


    // =====================================================
    // TRAFFIC LEVEL
    // =====================================================

    private String getTrafficLevel(
            Double trafficScore) {

        if (trafficScore == null) {

            return "LOW";
        }


        if (trafficScore < 30) {

            return "LOW";
        }


        if (trafficScore < 60) {

            return "MEDIUM";
        }


        return "HIGH";
    }


    // =====================================================
    // CONVERT MAP FEATURES
    // =====================================================

    private List<MapFeature> convertMapFeatures(
            Object value) {

        List<MapFeature> result =
                new ArrayList<>();


        if (!(value instanceof List)) {

            return result;
        }


        List<?> features =
                (List<?>) value;


        for (Object featureObject :
                features) {

            if (!(featureObject instanceof Map)) {

                continue;
            }


            Map<?, ?> feature =
                    (Map<?, ?>) featureObject;


            Long osmId = null;

            Object osmIdValue =
                    feature.get("osmId");

            if (osmIdValue instanceof Number) {

                osmId =
                        ((Number) osmIdValue)
                                .longValue();
            }


            String category =
                    feature.get("category") != null
                            ? feature.get("category")
                                    .toString()
                            : "OTHER";


            String name =
                    feature.get("name") != null
                            ? feature.get("name")
                                    .toString()
                            : "Unknown";


            Double latitude = null;

            Object latitudeValue =
                    feature.get("latitude");

            if (latitudeValue instanceof Number) {

                latitude =
                        ((Number) latitudeValue)
                                .doubleValue();
            }


            Double longitude = null;

            Object longitudeValue =
                    feature.get("longitude");

            if (longitudeValue instanceof Number) {

                longitude =
                        ((Number) longitudeValue)
                                .doubleValue();
            }


            if (latitude == null ||
                    longitude == null) {

                continue;
            }


            result.add(
                    new MapFeature(
                            osmId,
                            category,
                            name,
                            latitude,
                            longitude
                    )
            );
        }


        return result;
    }


    // =====================================================
    // MAP CONVERSION
    // =====================================================

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMap(
            Object value) {

        if (value instanceof Map) {

            return (Map<String, Object>) value;
        }

        return Map.of();
    }


    // =====================================================
    // STRING
    // =====================================================

    private String getString(
            Map<String, Object> map,
            String key,
            String defaultValue) {

        Object value =
                map.get(key);


        if (value == null) {

            return defaultValue;
        }


        return value.toString();
    }


    // =====================================================
    // DOUBLE
    // =====================================================

    private Double getDouble(
            Map<String, Object> map,
            String key) {

        Object value =
                map.get(key);


        if (value instanceof Number) {

            return (
                    (Number) value
            ).doubleValue();
        }


        return null;
    }


    // =====================================================
    // INTEGER
    // =====================================================

    private Integer getInteger(
            Map<String, Object> map,
            String key) {

        Object value =
                map.get(key);


        if (value instanceof Number) {

            return (
                    (Number) value
            ).intValue();
        }


        return null;
    }


    // =====================================================
    // COORDINATES
    // =====================================================

    private List<double[]> convertCoordinates(
            Object value) {

        List<double[]> result =
                new ArrayList<>();


        if (!(value instanceof List)) {

            return result;
        }


        List<?> points =
                (List<?>) value;


        for (Object pointObject :
                points) {

            if (!(pointObject instanceof List)) {

                continue;
            }


            List<?> point =
                    (List<?>) pointObject;


            if (point.size() < 2) {

                continue;
            }


            Object latitude =
                    point.get(0);

            Object longitude =
                    point.get(1);


            if (!(latitude instanceof Number) ||
                    !(longitude instanceof Number)) {

                continue;
            }


            result.add(
                    new double[] {

                            ((Number) latitude)
                                    .doubleValue(),

                            ((Number) longitude)
                                    .doubleValue()
                    }
            );
        }


        return result;
    }


    // =====================================================
    // RISK POINTS
    // =====================================================

    private List<Double> convertRiskPoints(
            Object value) {

        List<Double> result =
                new ArrayList<>();


        if (!(value instanceof List)) {

            return result;
        }


        List<?> points =
                (List<?>) value;


        for (Object point :
                points) {

            if (point instanceof Number) {

                result.add(
                        ((Number) point)
                                .doubleValue()
                );
            }
        }


        return result;
    }


    // =====================================================
    // HAS COORDINATES
    // =====================================================

    private boolean hasCoordinates(
            Map<String, Object> routeData) {

        Object coordinates =
                routeData.get(
                        "coordinates"
                );


        return coordinates instanceof List
                &&
                !((List<?>) coordinates)
                        .isEmpty();
    }
}