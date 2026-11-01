package com.womensecurity.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.womensecurity.backend.dto.LocationRequest;
import com.womensecurity.backend.entity.Location;
import com.womensecurity.backend.repository.LocationRepository;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    // CREATE
    public String saveLocation(LocationRequest request) {

        Location location = new Location();

        location.setUserId(request.getUserId());
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setUpdatedAt(LocalDateTime.now());
location.setIncidentId(request.getIncidentId());
location.setAccuracy(request.getAccuracy());
        locationRepository.save(location);

        return "Location Saved Successfully";
    }

    // READ USER LOCATION
    public List<Location> getUserLocations(Long userId) {

        return locationRepository.findByUserId(userId);

    }

    // READ ALL
    public List<Location> getAllLocations() {

        return locationRepository.findAll();

    }

    // UPDATE
    public Location updateLocation(Long id, Location updatedLocation) {

        Optional<Location> location = locationRepository.findById(id);

        if (location.isPresent()) {

            Location existing = location.get();

            existing.setLatitude(updatedLocation.getLatitude());
            existing.setLongitude(updatedLocation.getLongitude());
            existing.setUpdatedAt(LocalDateTime.now());

            return locationRepository.save(existing);
        }

        return null;
    }

    // DELETE
    public String deleteLocation(Long id) {

        if (locationRepository.existsById(id)) {

            locationRepository.deleteById(id);

            return "Location Deleted Successfully";
        }

        return "Location Not Found";
    }

}