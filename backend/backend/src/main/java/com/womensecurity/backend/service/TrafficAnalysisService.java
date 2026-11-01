package com.womensecurity.backend.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TrafficAnalysisService {

    private final HttpClient httpClient =
            HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    @Value("${tomtom.api.key}")
    private String apiKey;


    // =====================================================
    // TRAFFIC CACHE
    // =====================================================

    private final Map<String, CachedTraffic> trafficCache =
            new ConcurrentHashMap<>();

    private static final long CACHE_DURATION_MS =
            2 * 60 * 1000;


    public Double getTrafficRisk(
            Double latitude,
            Double longitude) {

        try {

            // =================================================
            // CACHE KEY
            // =================================================

            String cacheKey =
                    String.format(
                            "%.4f,%.4f",
                            latitude,
                            longitude
                    );


            // =================================================
            // CHECK CACHE
            // =================================================

            CachedTraffic cached =
                    trafficCache.get(cacheKey);

            if (cached != null) {

                long age =
                        System.currentTimeMillis()
                        - cached.timestamp;

                if (age < CACHE_DURATION_MS) {

                    System.out.println(
                            "TRAFFIC CACHE HIT: "
                            + cacheKey
                    );

                    return cached.risk;
                }

                trafficCache.remove(
                        cacheKey
                );
            }


            // =================================================
            // TOMTOM TRAFFIC FLOW API
            // =================================================

            String point =
                    latitude
                    + ","
                    + longitude;


            String encodedPoint =
                    URLEncoder.encode(
                            point,
                            StandardCharsets.UTF_8
                    );


            String url =
                    "https://api.tomtom.com/traffic/services/4/"
                    + "flowSegmentData/absolute/10/json"
                    + "?point="
                    + encodedPoint
                    + "&unit=KMPH"
                    + "&key="
                    + apiKey;


            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(url)
                            )
                            .timeout(
                                    Duration.ofSeconds(15)
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


            // =================================================
            // API ERROR
            // =================================================

            if (response.statusCode() != 200) {

                System.out.println(
                        "TRAFFIC API ERROR: "
                        + response.statusCode()
                );

                System.out.println(
                        "TRAFFIC API RESPONSE: "
                        + response.body()
                );

                // Do NOT cache failures.
                return null;
            }


            // =================================================
            // PARSE RESPONSE
            // =================================================

            JsonNode root =
                    objectMapper.readTree(
                            response.body()
                    );


            JsonNode flowSegment =
                    root.get(
                            "flowSegmentData"
                    );


            if (flowSegment == null ||
                    flowSegment.isNull()) {

                System.out.println(
                        "TRAFFIC DATA UNAVAILABLE"
                );

                return null;
            }


            // =================================================
            // CURRENT SPEED
            // =================================================

            JsonNode currentSpeedNode =
                    flowSegment.get(
                            "currentSpeed"
                    );


            // =================================================
            // FREE FLOW SPEED
            // =================================================

            JsonNode freeFlowSpeedNode =
                    flowSegment.get(
                            "freeFlowSpeed"
                    );


            if (currentSpeedNode == null ||
                    freeFlowSpeedNode == null) {

                System.out.println(
                        "TRAFFIC SPEED DATA MISSING"
                );

                return null;
            }


            double currentSpeed =
                    currentSpeedNode.asDouble();


            double freeFlowSpeed =
                    freeFlowSpeedNode.asDouble();


            System.out.println(
                    "CURRENT TRAFFIC SPEED: "
                    + currentSpeed
                    + " km/h"
            );


            System.out.println(
                    "FREE FLOW SPEED: "
                    + freeFlowSpeed
                    + " km/h"
            );


            // =================================================
            // ROAD CLOSURE
            // =================================================

            JsonNode roadClosureNode =
                    flowSegment.get(
                            "roadClosure"
                    );


            boolean roadClosure = false;


            if (roadClosureNode != null) {

                roadClosure =
                        roadClosureNode.asBoolean();
            }


            if (roadClosure) {

                System.out.println(
                        "ROAD CLOSURE DETECTED"
                );


                double trafficRisk =
                        100.0;


                trafficCache.put(
                        cacheKey,
                        new CachedTraffic(
                                trafficRisk
                        )
                );


                return trafficRisk;
            }


            // =================================================
            // INVALID FREE FLOW SPEED
            // =================================================

            if (freeFlowSpeed <= 0) {

                System.out.println(
                        "INVALID FREE FLOW SPEED"
                );

                return null;
            }


            // =================================================
            // CALCULATE CONGESTION
            // =================================================

            double congestion =
                    1.0
                    -
                    (
                        currentSpeed
                        /
                        freeFlowSpeed
                    );


            congestion =
                    Math.max(
                            0.0,
                            Math.min(
                                    congestion,
                                    1.0
                            )
                    );


            // =================================================
            // TRAFFIC RISK
            // =================================================

            double trafficRisk =
                    congestion * 100.0;


            trafficRisk =
                    Math.round(
                            trafficRisk * 10.0
                    ) / 10.0;


            System.out.println(
                    "TRAFFIC CONGESTION: "
                    + Math.round(
                            congestion * 100.0
                    )
                    + "%"
            );


            System.out.println(
                    "TRAFFIC RISK SCORE: "
                    + trafficRisk
            );


            // =================================================
            // SAVE SUCCESSFUL RESULT TO CACHE
            // =================================================

            trafficCache.put(
                    cacheKey,
                    new CachedTraffic(
                            trafficRisk
                    )
            );


            return trafficRisk;


        } catch (Exception e) {

            System.out.println(
                    "TRAFFIC SERVICE FAILED: "
                    + e.getMessage()
            );

            // Do NOT return 0.
            // null means traffic data unavailable.
            return null;
        }
    }


    // =====================================================
    // CACHE CLASS
    // =====================================================

    private static class CachedTraffic {

        private final double risk;
        private final long timestamp;


        public CachedTraffic(
                double risk) {

            this.risk =
                    risk;

            this.timestamp =
                    System.currentTimeMillis();
        }
    }
}