package com.womensecurity.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.womensecurity.backend.dto.RiskPredictionRequest;
import com.womensecurity.backend.model.RiskPredictionResponse;
import com.womensecurity.backend.service.RiskPredictionService;

@RestController
@RequestMapping("/api/risk-prediction")
@CrossOrigin(origins = "*")
public class RiskPredictionController {

    private final RiskPredictionService riskPredictionService;

    public RiskPredictionController(
            RiskPredictionService riskPredictionService) {

        this.riskPredictionService =
                riskPredictionService;
    }

    @PostMapping
    public ResponseEntity<RiskPredictionResponse>
    predictRisk(
            @RequestBody RiskPredictionRequest request) {

        return ResponseEntity.ok(
                riskPredictionService.predictRisk(
                        request
                )
        );
    }
}