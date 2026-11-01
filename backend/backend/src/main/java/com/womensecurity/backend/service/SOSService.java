package com.womensecurity.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.womensecurity.backend.dto.SOSRequest;
import com.womensecurity.backend.entity.SOSIncident;
import com.womensecurity.backend.repository.SOSRepository;

@Service
public class SOSService {

    @Autowired
    private SOSRepository sosRepository;

    // ---------------- CREATE ----------------
    public String activateSOS(SOSRequest request) {

        SOSIncident incident = new SOSIncident();

        incident.setUserId(request.getUserId());
        incident.setLatitude(request.getLatitude());
        incident.setLongitude(request.getLongitude());
        incident.setStatus(request.getStatus());

        incident.setCreatedAt(LocalDateTime.now());

        sosRepository.save(incident);

        return "SOS Activated Successfully";
    }

    // ---------------- READ BY USER ----------------
    public List<SOSIncident> getSOSHistory(Long userId) {

        return sosRepository.findByUserId(userId);

    }

    // ---------------- READ ALL ----------------
    public List<SOSIncident> getAllSOS() {

        return sosRepository.findAll();

    }

    // ---------------- UPDATE ----------------
    public SOSIncident updateSOS(Long id, SOSIncident updatedIncident) {

        Optional<SOSIncident> incident =
                sosRepository.findById(id);

        if (incident.isPresent()) {

            SOSIncident existing = incident.get();

            existing.setLatitude(updatedIncident.getLatitude());
            existing.setLongitude(updatedIncident.getLongitude());
            existing.setStatus(updatedIncident.getStatus());

            return sosRepository.save(existing);

        }

        return null;
    }

    // ---------------- DELETE ----------------
    public String deleteSOS(Long id) {

        if (sosRepository.existsById(id)) {

            sosRepository.deleteById(id);

            return "SOS Incident Deleted Successfully";
        }

        return "SOS Incident Not Found";
    }

}