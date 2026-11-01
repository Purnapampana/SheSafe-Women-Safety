package com.womensecurity.backend.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.womensecurity.backend.dto.LiveLocationRequest;
import com.womensecurity.backend.entity.EmergencyTracking;
import com.womensecurity.backend.entity.LiveLocation;
import com.womensecurity.backend.model.LiveLocationResponse;
import com.womensecurity.backend.repository.EmergencyTrackingRepository;
import com.womensecurity.backend.repository.LiveLocationRepository;

@Service
public class LiveLocationService {

    @Autowired
    private LiveLocationRepository liveLocationRepository;

    @Autowired
    private EmergencyTrackingRepository emergencyTrackingRepository;

    public LiveLocationResponse updateLocation(
            LiveLocationRequest request) {

        LiveLocationResponse response =
                new LiveLocationResponse();

        //--------------------------------------------------
        // Save Live Location History
        //--------------------------------------------------

        LiveLocation liveLocation = new LiveLocation();

        liveLocation.setUserId(request.getUserId());
        liveLocation.setLatitude(request.getLatitude());
        liveLocation.setLongitude(request.getLongitude());
        liveLocation.setRecordedAt(LocalDateTime.now());

        liveLocationRepository.save(liveLocation);

        //--------------------------------------------------
        // Update Current Emergency Tracking
        //--------------------------------------------------

        Optional<EmergencyTracking> trackingOptional =
                emergencyTrackingRepository
                        .findFirstByUserIdOrderByStartedAtDesc(
                                request.getUserId());

        if (trackingOptional.isPresent()) {

            EmergencyTracking tracking =
                    trackingOptional.get();

            tracking.setCurrentLatitude(
                    request.getLatitude());

            tracking.setCurrentLongitude(
                    request.getLongitude());

            emergencyTrackingRepository.save(tracking);

        }

        //--------------------------------------------------
        // Response
        //--------------------------------------------------

        response.setStatus("UPDATED");

        response.setMessage(
                "Live location updated successfully.");

        response.setLatitude(
                request.getLatitude());

        response.setLongitude(
                request.getLongitude());

        return response;

    }

}