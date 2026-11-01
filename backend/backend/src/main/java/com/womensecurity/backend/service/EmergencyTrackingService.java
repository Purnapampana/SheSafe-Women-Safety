package com.womensecurity.backend.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.womensecurity.backend.dto.EmergencyTrackingRequest;
import com.womensecurity.backend.entity.EmergencyTracking;
import com.womensecurity.backend.entity.Location;
import com.womensecurity.backend.model.EmergencyTrackingResponse;
import com.womensecurity.backend.repository.EmergencyTrackingRepository;
import com.womensecurity.backend.repository.LocationRepository;

@Service
public class EmergencyTrackingService {

    @Autowired
    private EmergencyTrackingRepository trackingRepository;

    @Autowired
    private LocationRepository locationRepository;

    public EmergencyTrackingResponse startTracking(
            EmergencyTrackingRequest request) {

        EmergencyTrackingResponse response =
                new EmergencyTrackingResponse();

        Optional<Location> location =
                locationRepository.findFirstByUserIdOrderByUpdatedAtDesc(
                        request.getUserId());

        if (location.isEmpty()) {

            response.setStatus("FAILED");
            response.setMessage("Location not found");

            return response;
        }

        EmergencyTracking tracking =
                new EmergencyTracking();

        tracking.setUserId(request.getUserId());

        tracking.setStatus("ACTIVE");

        tracking.setCurrentLatitude(location.get().getLatitude());

        tracking.setCurrentLongitude(location.get().getLongitude());

        tracking.setStartedAt(LocalDateTime.now());

        trackingRepository.save(tracking);

        response.setStatus("ACTIVE");

        response.setMessage("Emergency Tracking Started");

        response.setLatitude(location.get().getLatitude());

        response.setLongitude(location.get().getLongitude());

        return response;

    }

    public EmergencyTrackingResponse resolveTracking(
            EmergencyTrackingRequest request) {

        EmergencyTrackingResponse response =
                new EmergencyTrackingResponse();

        Optional<EmergencyTracking> tracking =
                trackingRepository
                        .findFirstByUserIdOrderByStartedAtDesc(
                                request.getUserId());

        if (tracking.isEmpty()) {

            response.setStatus("FAILED");

            response.setMessage("No active emergency");

            return response;
        }

        EmergencyTracking emergency =
                tracking.get();

        emergency.setStatus("RESOLVED");

        emergency.setResolvedAt(LocalDateTime.now());

        trackingRepository.save(emergency);

        response.setStatus("RESOLVED");

        response.setMessage("Emergency Resolved");

        response.setLatitude(
                emergency.getCurrentLatitude());

        response.setLongitude(
                emergency.getCurrentLongitude());

        return response;

    }

}