package com.womensecurity.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.womensecurity.backend.dto.ThreatRequest;
import com.womensecurity.backend.entity.ThreatDetection;
import com.womensecurity.backend.repository.ThreatRepository;

@Service
public class ThreatService {

    @Autowired
    private ThreatRepository threatRepository;

    // CREATE
    public String detectThreat(ThreatRequest request) {

        ThreatDetection threat = new ThreatDetection();

        threat.setUserId(request.getUserId());
        threat.setLatitude(request.getLatitude());
        threat.setLongitude(request.getLongitude());
        threat.setTimeOfDay(request.getTimeOfDay());

        int score = 0;

        if ("Night".equalsIgnoreCase(request.getTimeOfDay())) {
            score += 40;
        }

        if (request.getLatitude() > 17.30) {
            score += 20;
        }

        if (request.getLongitude() > 78.40) {
            score += 20;
        }

        threat.setDangerScore(score);

        if (score >= 70) {
            threat.setRiskLevel("HIGH");
        } else if (score >= 40) {
            threat.setRiskLevel("MEDIUM");
        } else {
            threat.setRiskLevel("LOW");
        }

        threat.setStatus("ACTIVE");
        threat.setCreatedAt(LocalDateTime.now());

        threatRepository.save(threat);

        return "Threat Analysis Completed";
    }

    // READ USER
    public List<ThreatDetection> getThreats(Long userId) {

        return threatRepository.findByUserId(userId);

    }

    // READ ALL
    public List<ThreatDetection> getAllThreats() {

        return threatRepository.findAll();

    }

    // UPDATE
    public ThreatDetection updateThreat(Long id, ThreatDetection updatedThreat) {

        Optional<ThreatDetection> threat =
                threatRepository.findById(id);

        if (threat.isPresent()) {

            ThreatDetection existing = threat.get();

            existing.setRiskLevel(updatedThreat.getRiskLevel());
            existing.setStatus(updatedThreat.getStatus());
            existing.setDangerScore(updatedThreat.getDangerScore());

            return threatRepository.save(existing);
        }

        return null;
    }

    // DELETE
    public String deleteThreat(Long id) {

        if (threatRepository.existsById(id)) {

            threatRepository.deleteById(id);

            return "Threat Record Deleted Successfully";
        }

        return "Threat Record Not Found";
    }

}