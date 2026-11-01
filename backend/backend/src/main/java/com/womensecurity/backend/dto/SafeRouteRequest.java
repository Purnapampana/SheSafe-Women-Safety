package com.womensecurity.backend.dto;

public class SafeRouteRequest {

    private Double startLatitude;
    private Double startLongitude;

    private String startAddress;

    private Double destinationLatitude;
    private Double destinationLongitude;

    private String destinationAddress;

    public SafeRouteRequest() {
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

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(
            String startAddress) {

        this.startAddress =
                startAddress;
    }

    public Double getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(
            Double destinationLatitude) {

        this.destinationLatitude =
                destinationLatitude;
    }

    public Double getDestinationLongitude() {
        return destinationLongitude;
    }

    public void setDestinationLongitude(
            Double destinationLongitude) {

        this.destinationLongitude =
                destinationLongitude;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(
            String destinationAddress) {

        this.destinationAddress =
                destinationAddress;
    }
}