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

import com.womensecurity.backend.dto.LocationRequest;
import com.womensecurity.backend.entity.Location;
import com.womensecurity.backend.service.LocationService;

@RestController
@RequestMapping("/api/location")
@CrossOrigin("*")
public class LocationController {

    @Autowired
    private LocationService locationService;

    // CREATE
    @PostMapping("/save")
    public String saveLocation(@RequestBody LocationRequest request) {

        return locationService.saveLocation(request);

    }

    // READ USER LOCATION
    @GetMapping("/user/{userId}")
    public List<Location> getUserLocations(@PathVariable Long userId) {

        return locationService.getUserLocations(userId);

    }

    // READ ALL
    @GetMapping("/all")
    public List<Location> getAllLocations() {

        return locationService.getAllLocations();

    }

    // UPDATE
    @PutMapping("/update/{id}")
    public Location updateLocation(
            @PathVariable Long id,
            @RequestBody Location location) {

        return locationService.updateLocation(id, location);

    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    public String deleteLocation(@PathVariable Long id) {

        return locationService.deleteLocation(id);

    }

}