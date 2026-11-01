package com.womensecurity.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.womensecurity.backend.dto.EmergencyTrackingRequest;
import com.womensecurity.backend.model.EmergencyTrackingResponse;
import com.womensecurity.backend.service.EmergencyTrackingService;

@RestController
@RequestMapping("/api/tracking")
@CrossOrigin("*")
public class EmergencyTrackingController {

    @Autowired
    private EmergencyTrackingService trackingService;

    @PostMapping("/start")
    public EmergencyTrackingResponse startTracking(
            @RequestBody EmergencyTrackingRequest request) {

        return trackingService.startTracking(request);

    }

    @PostMapping("/resolve")
    public EmergencyTrackingResponse resolveTracking(
            @RequestBody EmergencyTrackingRequest request) {

        return trackingService.resolveTracking(request);

    }

}