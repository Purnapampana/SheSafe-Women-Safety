package com.womensecurity.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.womensecurity.backend.dto.LiveLocationRequest;
import com.womensecurity.backend.model.LiveLocationResponse;
import com.womensecurity.backend.service.LiveLocationService;

@RestController
@RequestMapping("/api/live-location")
@CrossOrigin("*")
public class LiveLocationController {

    @Autowired
    private LiveLocationService liveLocationService;

    @PostMapping("/update")
    public LiveLocationResponse updateLocation(
            @RequestBody LiveLocationRequest request) {

        return liveLocationService.updateLocation(request);

    }

}