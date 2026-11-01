package com.womensecurity.backend.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GeocodingService {

    private final HttpClient httpClient =
            HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    public double[] geocodeAddress(String address) {

        try {

            String encodedAddress =
                    URLEncoder.encode(
                            address,
                            StandardCharsets.UTF_8
                    );

            String url =
                    "https://nominatim.openstreetmap.org/search"
                    + "?q="
                    + encodedAddress
                    + "&format=json"
                    + "&limit=1";

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .timeout(Duration.ofSeconds(15))
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
                        "GEOCODING API ERROR: "
                        + response.statusCode()
                );

                return null;
            }

            JsonNode results =
                    objectMapper.readTree(
                            response.body()
                    );

            if (!results.isArray() ||
                    results.isEmpty()) {

                System.out.println(
                        "ADDRESS NOT FOUND: "
                        + address
                );

                return null;
            }

            JsonNode location =
                    results.get(0);

            double latitude =
                    location.get("lat").asDouble();

            double longitude =
                    location.get("lon").asDouble();

            System.out.println(
                    "GEOCODED ADDRESS: "
                    + address
            );

            System.out.println(
                    "LATITUDE: "
                    + latitude
            );

            System.out.println(
                    "LONGITUDE: "
                    + longitude
            );

            return new double[] {
                    latitude,
                    longitude
            };

        } catch (Exception e) {

            System.out.println(
                    "GEOCODING FAILED: "
                    + e.getMessage()
            );

            return null;
        }
    }
}