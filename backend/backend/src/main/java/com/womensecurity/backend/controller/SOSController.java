package com.womensecurity.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.womensecurity.backend.dto.SOSRequest;
import com.womensecurity.backend.entity.SOSIncident;
import com.womensecurity.backend.service.SOSService;

@RestController
@RequestMapping("/api/sos")
@CrossOrigin("*")
public class SOSController {

    @Autowired
    private SOSService sosService;

    // ---------------- CREATE ----------------
    @PostMapping("/activate")
    public String activateSOS(@RequestBody SOSRequest request) {

        return sosService.activateSOS(request);

    }

    // ---------------- READ BY USER ----------------
    @GetMapping("/user/{userId}")
    public List<SOSIncident> getSOSHistory(@PathVariable Long userId) {

        return sosService.getSOSHistory(userId);

    }

    // ---------------- READ ALL ----------------
    @GetMapping("/all")
    public List<SOSIncident> getAllSOS() {

        return sosService.getAllSOS();

    }

    // ---------------- UPDATE ----------------
    @PutMapping("/update/{id}")
    public SOSIncident updateSOS(
            @PathVariable Long id,
            @RequestBody SOSIncident incident) {

        return sosService.updateSOS(id, incident);

    }

    // ---------------- DELETE ----------------
    @DeleteMapping("/delete/{id}")
    public String deleteSOS(@PathVariable Long id) {

        return sosService.deleteSOS(id);

    }

}