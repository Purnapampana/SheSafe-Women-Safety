package com.womensecurity.backend.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PopulationDensityService {

    private final HttpClient httpClient =
            HttpClient.newHttpClient();

    private final ObjectMapper objectMapper =
            new ObjectMapper();


    public double getPopulationDensityRisk(
            Double latitude,
            Double longitude) {

        try {

            double radius = 1000;

            String query =
                    "[out:json];"
                    + "("
                    + "nwr[\"building\"]"
                    + "(around:"
                    + radius
                    + ","
                    + latitude
                    + ","
                    + longitude
                    + ");"
                    + "nwr[\"landuse\"=\"residential\"]"
                    + "(around:"
                    + radius
                    + ","
                    + latitude
                    + ","
                    + longitude
                    + ");"
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
                        "Population API error: "
                        + response.statusCode()
                );

                return 0.0;
            }


            JsonNode root =
                    objectMapper.readTree(
                            response.body()
                    );


            JsonNode elements =
                    root.get("elements");


            if (elements == null ||
                    elements.isEmpty()) {

                return 0.0;
            }


            JsonNode tags =
                    elements.get(0)
                            .get("tags");


            if (tags == null) {
                return 0.0;
            }


            JsonNode total =
                    tags.get("total");


            if (total == null) {
                return 0.0;
            }


            int featureCount =
                    total.asInt();


            System.out.println(
                    "NEARBY BUILDING/RESIDENTIAL FEATURES: "
                    + featureCount
            );


            /*
             * Convert nearby mapped features
             * into a 0-100 density risk score.
             *
             * This is a density indicator,
             * not an exact population count.
             */

            double score;

            if (featureCount >= 1000) {

                score = 100.0;

            } else if (featureCount >= 500) {

                score = 80.0;

            } else if (featureCount >= 250) {

                score = 60.0;

            } else if (featureCount >= 100) {

                score = 40.0;

            } else if (featureCount >= 50) {

                score = 20.0;

            } else {

                score = 5.0;
            }


            System.out.println(
                    "POPULATION DENSITY RISK SCORE: "
                    + score
            );


            return score;


        } catch (Exception e) {

            System.out.println(
                    "Population density service failed: "
                    + e.getMessage()
            );

            return 0.0;
        }
    }
}