package com.womensecurity.backend.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class StreetLightingService {

    private final HttpClient httpClient =
            HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

    private final ObjectMapper objectMapper =
            new ObjectMapper();


    public Double getLightingRisk(
            Double latitude,
            Double longitude) {

        try {

            String query =
                    "[out:json][timeout:8];"
                    + "nwr[\"highway\"=\"street_lamp\"]"
                    + "(around:300,"
                    + latitude
                    + ","
                    + longitude
                    + ");"
                    + "out count;";


            String encodedQuery =
                    java.net.URLEncoder.encode(
                            query,
                            java.nio.charset.StandardCharsets.UTF_8
                    );


            String url =
                    "https://overpass-api.de/api/interpreter"
                    + "?data="
                    + encodedQuery;


            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .timeout(Duration.ofSeconds(12))
                            .header(
                                    "User-Agent",
                                    "SheSafe-Women-Safety-System"
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
                        "Street lighting data unavailable. HTTP: "
                        + response.statusCode()
                );

                return null;
            }


            JsonNode root =
                    objectMapper.readTree(
                            response.body()
                    );


            JsonNode elements =
                    root.get("elements");


            if (elements == null ||
                    elements.isEmpty()) {

                System.out.println(
                        "Street lighting data unavailable."
                );

                return null;
            }


            JsonNode tags =
                    elements.get(0)
                            .get("tags");


            if (tags == null) {
                return null;
            }


            JsonNode total =
                    tags.get("total");


            if (total == null) {
                return null;
            }


            int lampCount =
                    total.asInt();


            System.out.println(
                    "NEARBY MAPPED STREET LIGHTS: "
                    + lampCount
            );


            /*
             * More mapped street lights means
             * better lighting coverage.
             *
             * Therefore:
             * more lights = lower risk
             */

            double riskScore;


            if (lampCount >= 50) {

                riskScore = 5.0;

            } else if (lampCount >= 25) {

                riskScore = 20.0;

            } else if (lampCount >= 10) {

                riskScore = 40.0;

            } else if (lampCount >= 5) {

                riskScore = 65.0;

            } else {

                riskScore = 85.0;
            }


            System.out.println(
                    "STREET LIGHTING RISK SCORE: "
                    + riskScore
            );


            return riskScore;


        } catch (Exception e) {

            System.out.println(
                    "Street lighting data unavailable: "
                    + e.getMessage()
            );

            return null;
        }
    }
}