package com.womensecurity.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.womensecurity.backend.entity.RiskZone;
import com.womensecurity.backend.repository.RiskZoneRepository;

@Service
public class RiskZoneService {

    private final RiskZoneRepository riskZoneRepository;

    public RiskZoneService(
            RiskZoneRepository riskZoneRepository) {

        this.riskZoneRepository =
                riskZoneRepository;
    }

    public RiskZone saveRiskZone(
            RiskZone riskZone) {

        calculateRisk(riskZone);

        return riskZoneRepository.save(
                riskZone
        );
    }

    public List<RiskZone> getAllRiskZones() {

        return riskZoneRepository.findAll();
    }

    public List<RiskZone> getRiskZonesByLevel(
            String riskLevel) {

        return riskZoneRepository
                .findByRiskLevel(riskLevel);
    }

    private void calculateRisk(
            RiskZone riskZone) {

        double score = 0;

        if (riskZone.getCrimeReports() != null) {
            score += riskZone.getCrimeReports() * 2;
        }

        if (riskZone.getUserReports() != null) {
            score += riskZone.getUserReports() * 1.5;
        }

        if (Boolean.TRUE.equals(
                riskZone.getNightTime())) {

            score += 15;
        }

        if (Boolean.TRUE.equals(
                riskZone.getPoorLighting())) {

            score += 20;
        }

        if (Boolean.TRUE.equals(
                riskZone.getLowCrowd())) {

            score += 15;
        }

        if (score > 100) {
            score = 100;
        }

        riskZone.setRiskScore(score);

        if (score >= 70) {

            riskZone.setRiskLevel(
                    "CRIME HOTSPOT"
            );

        } else if (score >= 50) {

            riskZone.setRiskLevel(
                    "HIGH RISK"
            );

        } else if (score >= 25) {

            riskZone.setRiskLevel(
                    "MODERATE RISK"
            );

        } else {

            riskZone.setRiskLevel(
                    "SAFE ZONE"
            );
        }
    }
}