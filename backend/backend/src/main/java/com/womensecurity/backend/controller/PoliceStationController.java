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

import com.womensecurity.backend.dto.PoliceStationRequest;
import com.womensecurity.backend.entity.PoliceStation;
import com.womensecurity.backend.service.PoliceStationService;

@RestController
@RequestMapping("/api/police")
@CrossOrigin("*")
public class PoliceStationController {

    @Autowired
    private PoliceStationService policeStationService;

    // CREATE
    @PostMapping("/add")
    public String addStation(@RequestBody PoliceStationRequest request) {

        return policeStationService.addStation(request);

    }

    // READ ALL
    @GetMapping("/all")
    public List<PoliceStation> getAllStations() {

        return policeStationService.getAllStations();

    }

    // READ BY CITY
    @GetMapping("/city/{city}")
    public List<PoliceStation> getStationsByCity(@PathVariable String city) {

        return policeStationService.getStationsByCity(city);

    }

    // UPDATE
    @PutMapping("/update/{id}")
    public PoliceStation updateStation(
            @PathVariable Long id,
            @RequestBody PoliceStation station) {

        return policeStationService.updateStation(id, station);

    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    public String deleteStation(@PathVariable Long id) {

        return policeStationService.deleteStation(id);

    }
}