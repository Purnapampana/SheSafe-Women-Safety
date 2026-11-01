package com.womensecurity.backend.dto;

public class MapFeature {

    private Long osmId;

    private String category;

    private String name;

    private Double latitude;

    private Double longitude;


    public MapFeature() {
    }


    public MapFeature(
            Long osmId,
            String category,
            String name,
            Double latitude,
            Double longitude) {

        this.osmId = osmId;
        this.category = category;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public Long getOsmId() {
        return osmId;
    }


    public void setOsmId(Long osmId) {
        this.osmId = osmId;
    }


    public String getCategory() {
        return category;
    }


    public void setCategory(String category) {
        this.category = category;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public Double getLatitude() {
        return latitude;
    }


    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }


    public Double getLongitude() {
        return longitude;
    }


    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}