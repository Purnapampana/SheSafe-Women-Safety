package com.womensecurity.backend.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WeatherService {

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    private final HttpClient httpClient =
            HttpClient.newHttpClient();


    public double getWeatherRisk(
            Double latitude,
            Double longitude) {

        try {

            String url =
                    "https://api.open-meteo.com/v1/forecast"
                    + "?latitude=" + latitude
                    + "&longitude=" + longitude
                    + "&current=temperature_2m,precipitation,"
                    + "wind_speed_10m,weather_code";


            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .GET()
                            .build();


            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );


            if (response.statusCode() != 200) {

                System.out.println(
                        "Weather API error: "
                        + response.statusCode()
                );

                return 2.0;
            }


            JsonNode root =
                    objectMapper.readTree(
                            response.body()
                    );


            JsonNode current =
                    root.get("current");


            double precipitation =
                    current
                            .get("precipitation")
                            .asDouble();


            double windSpeed =
                    current
                            .get("wind_speed_10m")
                            .asDouble();


            int weatherCode =
                    current
                            .get("weather_code")
                            .asInt();


            double score = 0;


            /*
             * Rain
             */
            if (precipitation > 0) {
                score += 25;
            }


            /*
             * Strong wind
             */
            if (windSpeed >= 30) {
                score += 25;
            } else if (windSpeed >= 20) {
                score += 15;
            }


            /*
             * Bad weather conditions
             */
            if (weatherCode >= 51 &&
                    weatherCode <= 67) {

                score += 25;

            } else if (weatherCode >= 80 &&
                    weatherCode <= 82) {

                score += 30;

            } else if (weatherCode >= 95) {

                score += 40;
            }

System.out.println(
        "WEATHER RISK SCORE: " + score
);
            return Math.min(score, 100.0);


        } catch (Exception e) {

            System.out.println(
                    "Weather API failed: "
                    + e.getMessage()
            );

            /*
             * Keep the risk engine working
             * if the weather service is unavailable.
             */
            return 2.0;
        }
    }
}