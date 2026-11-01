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

import com.womensecurity.backend.dto.CrimeHotspotRequest;
import com.womensecurity.backend.entity.CrimeHotspot;
import com.womensecurity.backend.service.CrimeHotspotService;

@RestController
@RequestMapping("/api/crime")
@CrossOrigin("*")
public class CrimeHotspotController {

    @Autowired
    private CrimeHotspotService crimeHotspotService;

    // CREATE
    @PostMapping("/add")
    public String addHotspot(@RequestBody CrimeHotspotRequest request) {

        return crimeHotspotService.addHotspot(request);

    }

    // READ ALL
    @GetMapping("/all")
    public List<CrimeHotspot> getAllHotspots() {

        return crimeHotspotService.getAllHotspots();

    }

    // READ BY RISK LEVEL
    @GetMapping("/risk/{riskLevel}")
    public List<CrimeHotspot> getByRiskLevel(@PathVariable String riskLevel) {

        return crimeHotspotService.getByRiskLevel(riskLevel);

    }

    // UPDATE
    @PutMapping("/update/{id}")
    public CrimeHotspot updateHotspot(
            @PathVariable Long id,
            @RequestBody CrimeHotspot hotspot) {

        return crimeHotspotService.updateHotspot(id, hotspot);

    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    public String deleteHotspot(@PathVariable Long id) {

        return crimeHotspotService.deleteHotspot(id);

    }

}