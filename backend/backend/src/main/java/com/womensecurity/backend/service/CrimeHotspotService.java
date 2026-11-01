package com.womensecurity.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.womensecurity.backend.dto.CrimeHotspotRequest;
import com.womensecurity.backend.entity.CrimeHotspot;
import com.womensecurity.backend.repository.CrimeHotspotRepository;

@Service
public class CrimeHotspotService {

    @Autowired
    private CrimeHotspotRepository crimeHotspotRepository;

    // CREATE
    public String addHotspot(CrimeHotspotRequest request) {

        CrimeHotspot hotspot = new CrimeHotspot();

        hotspot.setLocationName(request.getLocationName());
        hotspot.setLatitude(request.getLatitude());
        hotspot.setLongitude(request.getLongitude());
        hotspot.setCrimeType(request.getCrimeType());
        hotspot.setRiskLevel(request.getRiskLevel());
        hotspot.setDescription(request.getDescription());
        hotspot.setCreatedAt(LocalDateTime.now());

        crimeHotspotRepository.save(hotspot);

        return "Crime Hotspot Added Successfully";
    }

    // READ ALL
    public List<CrimeHotspot> getAllHotspots() {

        return crimeHotspotRepository.findAll();

    }

    // READ BY RISK
    public List<CrimeHotspot> getByRiskLevel(String riskLevel) {

        return crimeHotspotRepository.findByRiskLevel(riskLevel);

    }

    // UPDATE
    public CrimeHotspot updateHotspot(Long id, CrimeHotspot updatedHotspot) {

        Optional<CrimeHotspot> hotspot =
                crimeHotspotRepository.findById(id);

        if (hotspot.isPresent()) {

            CrimeHotspot existing = hotspot.get();

            existing.setLocationName(updatedHotspot.getLocationName());
            existing.setLatitude(updatedHotspot.getLatitude());
            existing.setLongitude(updatedHotspot.getLongitude());
            existing.setCrimeType(updatedHotspot.getCrimeType());
            existing.setRiskLevel(updatedHotspot.getRiskLevel());
            existing.setDescription(updatedHotspot.getDescription());

            return crimeHotspotRepository.save(existing);
        }

        return null;
    }

    // DELETE
    public String deleteHotspot(Long id) {

        if (crimeHotspotRepository.existsById(id)) {

            crimeHotspotRepository.deleteById(id);

            return "Crime Hotspot Deleted Successfully";
        }

        return "Crime Hotspot Not Found";
    }

}