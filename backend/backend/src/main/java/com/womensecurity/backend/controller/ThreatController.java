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

import com.womensecurity.backend.dto.ThreatRequest;
import com.womensecurity.backend.entity.ThreatDetection;
import com.womensecurity.backend.service.ThreatService;

@RestController
@RequestMapping("/api/threat")
@CrossOrigin("*")
public class ThreatController {

    @Autowired
    private ThreatService threatService;

    // CREATE
    @PostMapping("/detect")
    public String detectThreat(@RequestBody ThreatRequest request) {

        return threatService.detectThreat(request);

    }

    // READ USER
    @GetMapping("/user/{userId}")
    public List<ThreatDetection> getThreats(@PathVariable Long userId) {

        return threatService.getThreats(userId);

    }

    // READ ALL
    @GetMapping("/all")
    public List<ThreatDetection> getAllThreats() {

        return threatService.getAllThreats();

    }

    // UPDATE
    @PutMapping("/update/{id}")
    public ThreatDetection updateThreat(
            @PathVariable Long id,
            @RequestBody ThreatDetection threat) {

        return threatService.updateThreat(id, threat);

    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    public String deleteThreat(@PathVariable Long id) {

        return threatService.deleteThreat(id);

    }

}