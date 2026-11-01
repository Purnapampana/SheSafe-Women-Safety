package com.womensecurity.backend.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class RouteSafetyFeatureService {


    private final HttpClient httpClient =
            HttpClient.newBuilder()
                    .connectTimeout(
                            Duration.ofSeconds(10)
                    )
                    .build();


    private final ObjectMapper objectMapper =
            new ObjectMapper();


    // =====================================================
    // CACHE
    // =====================================================

    private final Map<String, CachedFeatures>
            featureCache =
            new ConcurrentHashMap<>();


    private static final long
            CACHE_DURATION_MS =
            5 * 60 * 1000;


    // =====================================================
    // ANALYZE LOCATION
    // =====================================================

    public RouteSafetyFeatures
    analyzeLocation(
            Double latitude,
            Double longitude) {


        String cacheKey =
                String.format(
                        "%.4f,%.4f",
                        latitude,
                        longitude
                );


        // =================================================
        // CACHE CHECK
        // =================================================

        CachedFeatures cached =
                featureCache.get(
                        cacheKey
                );


        if (cached != null) {

            long age =
                    System.currentTimeMillis()
                    - cached.timestamp;


            if (age < CACHE_DURATION_MS) {

                System.out.println(
                        "ROUTE SAFETY FEATURE CACHE HIT: "
                        + cacheKey
                );

                return cached.features;
            }


            featureCache.remove(
                    cacheKey
            );
        }


        try {

            // =================================================
            // ONE OVERPASS REQUEST
            // =================================================

            String query =
        "[out:json][timeout:20];"
        + "("

        // =============================================
        // STREET LIGHTS
        // =============================================

        + "node[\"highway\"=\"street_lamp\"]"
        + "(around:200,"
        + latitude
        + ","
        + longitude
        + ");"

        // =============================================
        // CCTV
        // =============================================

        + "node[\"man_made\"=\"surveillance\"]"
        + "(around:300,"
        + latitude
        + ","
        + longitude
        + ");"

        // =============================================
        // POLICE
        // =============================================

        + "nwr[\"amenity\"=\"police\"]"
        + "(around:1000,"
        + latitude
        + ","
        + longitude
        + ");"

        // =============================================
        // CROWD LOCATIONS
        // =============================================

        + "nwr[\"amenity\"~"
        + "\"restaurant|cafe|fast_food|"
        + "school|hospital|marketplace|"
        + "bus_station|cinema|theatre|"
        + "college|university\"]"
        + "(around:500,"
        + latitude
        + ","
        + longitude
        + ");"

        // =============================================
        // BUS STOPS
        // =============================================

        + "node[\"highway\"=\"bus_stop\"]"
        + "(around:500,"
        + latitude
        + ","
        + longitude
        + ");"

        + ");"

        + "out center tags;";


            String encodedQuery =
                    URLEncoder.encode(
                            query,
                            StandardCharsets.UTF_8
                    );


            String url =
        "https://maps.mail.ru/osm/tools/overpass/api/interpreter";


            HttpRequest request =
        HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(
                        Duration.ofSeconds(25)
                )
                .header(
                        "User-Agent",
                        "SheSafe-Women-Safety-System/1.0"
                )
                .header(
                        "Content-Type",
                        "application/x-www-form-urlencoded"
                )
                .POST(
                        HttpRequest.BodyPublishers.ofString(
                                "data=" + encodedQuery
                        )
                )
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
                        "ROUTE SAFETY FEATURE API ERROR: "
                        + response.statusCode()
                );

                return new RouteSafetyFeatures();
            }


            // =================================================
            // PARSE RESPONSE
            // =================================================

            JsonNode root =
                    objectMapper.readTree(
                            response.body()
                    );


            JsonNode elements =
                    root.get("elements");


            if (elements == null ||
                    !elements.isArray()) {

                return new RouteSafetyFeatures();
            }


            // =================================================
            // COUNTS
            // =================================================

            int lightingCount = 0;

            int cctvCount = 0;

            int policeCount = 0;

            int crowdIndicatorCount = 0;


            // =================================================
            // LOCATIONS
            // =================================================

            List<double[]> lightingLocations =
                    new ArrayList<>();

            List<double[]> cctvLocations =
                    new ArrayList<>();

            List<double[]> crowdLocations =
                    new ArrayList<>();

            List<double[]> policeLocations =
                    new ArrayList<>();


            // =================================================
            // PROCESS FEATURES
            // =================================================

            for (JsonNode element :
                    elements) {


                JsonNode tags =
                        element.get("tags");


                if (tags == null) {
                    continue;
                }


                // =================================================
                // GET LOCATION
                // =================================================

                double featureLatitude =
                        0.0;

                double featureLongitude =
                        0.0;

                boolean hasLocation =
                        false;


                // =================================================
                // NODE
                // =================================================

                JsonNode latNode =
                        element.get("lat");

                JsonNode lonNode =
                        element.get("lon");


                if (latNode != null &&
                        lonNode != null) {

                    featureLatitude =
                            latNode.asDouble();

                    featureLongitude =
                            lonNode.asDouble();

                    hasLocation = true;
                }


                // =================================================
                // WAY / AREA CENTER
                // =================================================

                if (!hasLocation) {

                    JsonNode center =
                            element.get("center");


                    if (center != null) {

                        JsonNode centerLat =
                                center.get("lat");

                        JsonNode centerLon =
                                center.get("lon");


                        if (centerLat != null &&
                                centerLon != null) {

                            featureLatitude =
                                    centerLat.asDouble();

                            featureLongitude =
                                    centerLon.asDouble();

                            hasLocation = true;
                        }
                    }
                }


                // =================================================
                // STREET LIGHT
                // =================================================

                JsonNode highway =
                        tags.get("highway");


                if (highway != null &&
                        "street_lamp".equals(
                                highway.asText()
                        )) {

                    lightingCount++;


                    if (hasLocation) {

                        lightingLocations.add(
                                new double[] {
                                        featureLatitude,
                                        featureLongitude
                                }
                        );
                    }
                }


                // =================================================
                // CCTV
                // =================================================

                JsonNode manMade =
                        tags.get("man_made");


                if (manMade != null &&
                        "surveillance".equals(
                                manMade.asText()
                        )) {


                    JsonNode surveillanceType =
                            tags.get(
                                    "surveillance:type"
                            );


                    if (surveillanceType == null ||
                            "camera".equalsIgnoreCase(
                                    surveillanceType.asText()
                            )) {

                        cctvCount++;


                        if (hasLocation) {

                            cctvLocations.add(
                                    new double[] {
                                            featureLatitude,
                                            featureLongitude
                                    }
                            );
                        }
                    }
                }


                // =================================================
                // POLICE
                // =================================================

                JsonNode amenity =
                        tags.get("amenity");


                if (amenity != null &&
                        "police".equals(
                                amenity.asText()
                        )) {

                    policeCount++;


                    if (hasLocation) {

                        policeLocations.add(
                                new double[] {
                                        featureLatitude,
                                        featureLongitude
                                }
                        );
                    }
                }


                // =================================================
                // CROWD INDICATORS
                // =================================================

                if (amenity != null) {

                    String value =
                            amenity.asText();


                    if (
                            value.equals("restaurant") ||
                            value.equals("cafe") ||
                            value.equals("fast_food") ||
                            value.equals("school") ||
                            value.equals("hospital") ||
                            value.equals("marketplace") ||
                            value.equals("bus_station") ||
                            value.equals("cinema") ||
                            value.equals("theatre") ||
                            value.equals("college") ||
                            value.equals("university")
                    ) {

                        crowdIndicatorCount++;


                        if (hasLocation) {

                            crowdLocations.add(
                                    new double[] {
                                            featureLatitude,
                                            featureLongitude
                                    }
                            );
                        }
                    }
                }


                // =================================================
                // BUS STOP
                // =================================================

                if (highway != null &&
                        "bus_stop".equals(
                                highway.asText()
                        )) {

                    crowdIndicatorCount++;


                    if (hasLocation) {

                        crowdLocations.add(
                                new double[] {
                                        featureLatitude,
                                        featureLongitude
                                }
                        );
                    }
                }
            }


            // =================================================
            // LOG RESULTS
            // =================================================

            System.out.println(
                    "ROUTE LIGHTING FEATURES: "
                    + lightingCount
            );


            System.out.println(
                    "ROUTE CCTV FEATURES: "
                    + cctvCount
            );


            System.out.println(
                    "ROUTE CROWD INDICATORS: "
                    + crowdIndicatorCount
            );


            System.out.println(
                    "ROUTE POLICE STATIONS: "
                    + policeCount
            );


            // =================================================
            // CREATE RESULT
            // =================================================

            RouteSafetyFeatures features =
                    new RouteSafetyFeatures(

                            lightingCount,

                            cctvCount,

                            crowdIndicatorCount,

                            policeCount,

                            lightingLocations,

                            cctvLocations,

                            crowdLocations,

                            policeLocations
                    );


            // =================================================
            // CACHE
            // =================================================

            featureCache.put(
                    cacheKey,
                    new CachedFeatures(
                            features
                    )
            );


            return features;


        } catch (Exception e) {

            System.out.println(
                    "ROUTE SAFETY FEATURE SERVICE FAILED: "
                    + e.getMessage()
            );


            return new RouteSafetyFeatures();
        }
    }


    // =====================================================
    // RESULT CLASS
    // =====================================================

   public static class RouteSafetyFeatures {

   private Integer lightingCount;
private Integer cctvCount;
private Integer crowdIndicatorCount;
private Integer policeStationCount;

    private List<double[]> lightingLocations;
    private List<double[]> cctvLocations;
    private List<double[]> crowdLocations;
    private List<double[]> policeLocations;


    // =====================================================
    // DEFAULT CONSTRUCTOR
    // =====================================================

   public RouteSafetyFeatures() {

    this.lightingCount = null;
    this.cctvCount = null;
    this.crowdIndicatorCount = null;
    this.policeStationCount = null;

    this.lightingLocations =
            new ArrayList<>();

    this.cctvLocations =
            new ArrayList<>();

    this.crowdLocations =
            new ArrayList<>();

    this.policeLocations =
            new ArrayList<>();
}


    // =====================================================
    // OLD CONSTRUCTOR
    // =====================================================

    public RouteSafetyFeatures(
            int lightingCount,
            int cctvCount,
            int crowdIndicatorCount,
            int policeStationCount) {

        this();

        this.lightingCount =
                lightingCount;

        this.cctvCount =
                cctvCount;

        this.crowdIndicatorCount =
                crowdIndicatorCount;

        this.policeStationCount =
                policeStationCount;
    }


    // =====================================================
    // NEW CONSTRUCTOR
    // =====================================================

    public RouteSafetyFeatures(
            int lightingCount,
            int cctvCount,
            int crowdIndicatorCount,
            int policeStationCount,
            List<double[]> lightingLocations,
            List<double[]> cctvLocations,
            List<double[]> crowdLocations,
            List<double[]> policeLocations) {

        this.lightingCount =
                lightingCount;

        this.cctvCount =
                cctvCount;

        this.crowdIndicatorCount =
                crowdIndicatorCount;

        this.policeStationCount =
                policeStationCount;

        this.lightingLocations =
                lightingLocations != null
                        ? lightingLocations
                        : new ArrayList<>();

        this.cctvLocations =
                cctvLocations != null
                        ? cctvLocations
                        : new ArrayList<>();

        this.crowdLocations =
                crowdLocations != null
                        ? crowdLocations
                        : new ArrayList<>();

        this.policeLocations =
                policeLocations != null
                        ? policeLocations
                        : new ArrayList<>();
    }


    // =====================================================
    // COUNT GETTERS
    // =====================================================

    public Integer getLightingCount() {
        return lightingCount;
    }

    public Integer getCctvCount() {
        return cctvCount;
    }

    public Integer getCrowdIndicatorCount() {
        return crowdIndicatorCount;
    }

    public Integer getPoliceStationCount() {
        return policeStationCount;
    }


    // =====================================================
    // COUNT SETTERS
    // =====================================================

    public void setLightingCount(
            Integer lightingCount) {

        this.lightingCount =
                lightingCount;
    }

    public void setCctvCount(
            Integer cctvCount) {

        this.cctvCount =
                cctvCount;
    }

    public void setCrowdIndicatorCount(
            Integer crowdIndicatorCount) {

        this.crowdIndicatorCount =
                crowdIndicatorCount;
    }

    public void setPoliceStationCount(
            Integer policeStationCount) {

        this.policeStationCount =
                policeStationCount;
    }


    // =====================================================
    // LOCATION GETTERS
    // =====================================================

    public List<double[]> getLightingLocations() {
        return lightingLocations;
    }

    public List<double[]> getCctvLocations() {
        return cctvLocations;
    }

    public List<double[]> getCrowdLocations() {
        return crowdLocations;
    }

    public List<double[]> getPoliceLocations() {
        return policeLocations;
    }


    // =====================================================
    // LOCATION SETTERS
    // =====================================================

    public void setLightingLocations(
            List<double[]> lightingLocations) {

        this.lightingLocations =
                lightingLocations;
    }

    public void setCctvLocations(
            List<double[]> cctvLocations) {

        this.cctvLocations =
                cctvLocations;
    }

    public void setCrowdLocations(
            List<double[]> crowdLocations) {

        this.crowdLocations =
                crowdLocations;
    }

    public void setPoliceLocations(
            List<double[]> policeLocations) {

        this.policeLocations =
                policeLocations;
    }
}


    // =====================================================
    // CACHE CLASS
    // =====================================================

    private static class CachedFeatures {


        private final RouteSafetyFeatures features;

        private final long timestamp;


        public CachedFeatures(
                RouteSafetyFeatures features) {

            this.features =
                    features;


            this.timestamp =
                    System.currentTimeMillis();
        }
    }
}