package com.womensecurity.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.womensecurity.backend.dto.SafeRouteRequest;
import com.womensecurity.backend.model.SafeRouteResponse;
import com.womensecurity.backend.service.SafeRouteService;

@RestController
@RequestMapping("/api/safe-route")
@CrossOrigin(origins = "*")
public class SafeRouteController {

    private final SafeRouteService safeRouteService;

    public SafeRouteController(
            SafeRouteService safeRouteService) {

        this.safeRouteService =
                safeRouteService;
    }

    @PostMapping
    public ResponseEntity<SafeRouteResponse>
    recommendRoute(
            @RequestBody SafeRouteRequest request) {

        return ResponseEntity.ok(
                safeRouteService.recommendRoute(
                        request
                )
        );
    }
}