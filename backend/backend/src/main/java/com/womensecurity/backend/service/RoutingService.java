package com.womensecurity.backend.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RoutingService {

    private final HttpClient httpClient =
            HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

    private final ObjectMapper objectMapper =
            new ObjectMapper();


    public List<RouteData> getRoutes(
            Double startLatitude,
            Double startLongitude,
            Double destinationLatitude,
            Double destinationLongitude) {

        List<RouteData> routes =
                new ArrayList<>();

        try {

            /*
             * OSRM expects:
             *
             * longitude,latitude;
             * longitude,latitude
             */

            String coordinates =
                    startLongitude
                    + ","
                    + startLatitude
                    + ";"
                    + destinationLongitude
                    + ","
                    + destinationLatitude;


            /*
             * Request alternative routes
             * and return the actual road geometry.
             */

            String url =
                    "https://router.project-osrm.org"
                    + "/route/v1/driving/"
                    + coordinates
                    + "?alternatives=2"
                    + "&overview=full"
                    + "&geometries=geojson";


            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .timeout(
                                    Duration.ofSeconds(20)
                            )
                            .header(
                                    "User-Agent",
                                    "SheSafe-Women-Safety-System/1.0"
                            )
                            .GET()
                            .build();


            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );


            if (response.statusCode() != 200) {

                System.out.println(
                        "ROUTING API ERROR: "
                        + response.statusCode()
                );

                return routes;
            }


            JsonNode root =
                    objectMapper.readTree(
                            response.body()
                    );


            String code =
                    root.get("code").asText();


            if (!"Ok".equals(code)) {

                System.out.println(
                        "ROUTING API RESULT: "
                        + code
                );

                return routes;
            }


            JsonNode routeArray =
                    root.get("routes");


            if (routeArray == null ||
                    routeArray.isEmpty()) {

                System.out.println(
                        "No routes found."
                );

                return routes;
            }


            int routeNumber = 1;


            for (JsonNode route :
                    routeArray) {


                // =====================================================
                // DISTANCE
                // =====================================================

                double distanceKm =
                        route.get("distance")
                                .asDouble()
                        / 1000.0;


                // =====================================================
                // DURATION
                // =====================================================

                int durationMinutes =
                        (int) Math.round(
                                route.get("duration")
                                        .asDouble()
                                / 60.0
                        );


                // =====================================================
                // ROUTE GEOMETRY
                // =====================================================

                List<double[]> coordinatesList =
                        new ArrayList<>();


                JsonNode geometry =
                        route.get("geometry");


                if (geometry != null) {

                    JsonNode routeCoordinates =
                            geometry.get("coordinates");


                    if (routeCoordinates != null &&
                            routeCoordinates.isArray()) {


                        for (JsonNode coordinate :
                                routeCoordinates) {


                            /*
                             * GeoJSON uses:
                             *
                             * [longitude, latitude]
                             */

                            if (coordinate.size() >= 2) {

                                double longitude =
                                        coordinate
                                                .get(0)
                                                .asDouble();


                                double latitude =
                                        coordinate
                                                .get(1)
                                                .asDouble();


                                coordinatesList.add(
                                        new double[] {
                                                latitude,
                                                longitude
                                        }
                                );
                            }
                        }
                    }
                }


                // =====================================================
                // CREATE ROUTE DATA
                // =====================================================

                routes.add(
                        new RouteData(
                                "Route "
                                + routeNumber,
                                distanceKm,
                                durationMinutes,
                                coordinatesList
                        )
                );


                System.out.println(
                        "ROUTE "
                        + routeNumber
                        + " DISTANCE: "
                        + distanceKm
                        + " km"
                );


                System.out.println(
                        "ROUTE "
                        + routeNumber
                        + " DURATION: "
                        + durationMinutes
                        + " minutes"
                );


                System.out.println(
                        "ROUTE "
                        + routeNumber
                        + " POINTS: "
                        + coordinatesList.size()
                );


                routeNumber++;
            }


            System.out.println(
                    "REAL ROUTES FOUND: "
                    + routes.size()
            );


            return routes;


        } catch (Exception e) {

            System.out.println(
                    "ROUTING SERVICE FAILED: "
                    + e.getMessage()
            );

            return routes;
        }
    }


    // =====================================================
    // ROUTE DATA
    // =====================================================

    public static class RouteData {

        private String routeName;

        private Double distanceKm;

        private Integer durationMinutes;

        private List<double[]> coordinates;


        public RouteData(
                String routeName,
                Double distanceKm,
                Integer durationMinutes,
                List<double[]> coordinates) {

            this.routeName =
                    routeName;

            this.distanceKm =
                    distanceKm;

            this.durationMinutes =
                    durationMinutes;

            this.coordinates =
                    coordinates;
        }


        public String getRouteName() {

            return routeName;
        }


        public Double getDistanceKm() {

            return distanceKm;
        }


        public Integer getDurationMinutes() {

            return durationMinutes;
        }


        public List<double[]> getCoordinates() {

            return coordinates;
        }
    }
}