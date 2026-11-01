package com.womensecurity.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.womensecurity.backend.entity.RiskZone;
import com.womensecurity.backend.service.RiskZoneService;

@RestController
@RequestMapping("/api/risk-zones")
@CrossOrigin(origins = "*")
public class RiskZoneController {

    private final RiskZoneService riskZoneService;

    public RiskZoneController(
            RiskZoneService riskZoneService) {

        this.riskZoneService =
                riskZoneService;
    }

    @PostMapping
    public ResponseEntity<RiskZone> saveRiskZone(
            @RequestBody RiskZone riskZone) {

        return ResponseEntity.ok(
                riskZoneService.saveRiskZone(
                        riskZone
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<RiskZone>> getAllRiskZones() {

        return ResponseEntity.ok(
                riskZoneService.getAllRiskZones()
        );
    }

    @GetMapping("/level/{riskLevel}")
    public ResponseEntity<List<RiskZone>>
    getRiskZonesByLevel(
            @PathVariable String riskLevel) {

        return ResponseEntity.ok(
                riskZoneService
                        .getRiskZonesByLevel(
                                riskLevel
                        )
        );
    }
}