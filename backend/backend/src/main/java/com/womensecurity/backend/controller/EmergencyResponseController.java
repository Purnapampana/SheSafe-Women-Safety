package com.womensecurity.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.womensecurity.backend.dto.EmergencyResponseRequest;
import com.womensecurity.backend.model.EmergencyResponse;
import com.womensecurity.backend.service.EmergencyResponseService;

@RestController
@RequestMapping("/api/emergency-response")
@CrossOrigin("*")
public class EmergencyResponseController {

    @Autowired
    private EmergencyResponseService emergencyResponseService;

    @PostMapping("/generate")
    public EmergencyResponse generateResponse(
            @RequestBody EmergencyResponseRequest request) {

        return emergencyResponseService.generateResponse(request);

    }

}