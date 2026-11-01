package com.womensecurity.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.womensecurity.backend.dto.PoliceStationRequest;
import com.womensecurity.backend.entity.PoliceStation;
import com.womensecurity.backend.repository.PoliceStationRepository;

@Service
public class PoliceStationService {

    @Autowired
    private PoliceStationRepository policeStationRepository;

    // CREATE
    public String addStation(PoliceStationRequest request) {

        PoliceStation station = new PoliceStation();

        station.setStationName(request.getStationName());
        station.setAddress(request.getAddress());
        station.setPhoneNumber(request.getPhoneNumber());
        station.setLatitude(request.getLatitude());
        station.setLongitude(request.getLongitude());
        station.setCity(request.getCity());
        station.setCreatedAt(LocalDateTime.now());

        policeStationRepository.save(station);

        return "Police Station Added Successfully";
    }

    // READ ALL
    public List<PoliceStation> getAllStations() {

        return policeStationRepository.findAll();

    }

    // READ BY CITY
    public List<PoliceStation> getStationsByCity(String city) {

        return policeStationRepository.findByCity(city);

    }

    // UPDATE
    public PoliceStation updateStation(Long id, PoliceStation updatedStation) {

        Optional<PoliceStation> station = policeStationRepository.findById(id);

        if (station.isPresent()) {

            PoliceStation existing = station.get();

            existing.setStationName(updatedStation.getStationName());
            existing.setAddress(updatedStation.getAddress());
            existing.setPhoneNumber(updatedStation.getPhoneNumber());
            existing.setLatitude(updatedStation.getLatitude());
            existing.setLongitude(updatedStation.getLongitude());
            existing.setCity(updatedStation.getCity());

            return policeStationRepository.save(existing);
        }

        return null;
    }

    // DELETE
    public String deleteStation(Long id) {

        if (policeStationRepository.existsById(id)) {

            policeStationRepository.deleteById(id);

            return "Police Station Deleted Successfully";
        }

        return "Police Station Not Found";
    }
}