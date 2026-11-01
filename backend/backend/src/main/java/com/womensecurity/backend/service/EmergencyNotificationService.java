package com.womensecurity.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.womensecurity.backend.dto.NotificationRequest;
import com.womensecurity.backend.entity.EmergencyContact;
import com.womensecurity.backend.entity.Location;
import com.womensecurity.backend.entity.NotificationLog;
import com.womensecurity.backend.entity.ThreatDetection;
import com.womensecurity.backend.model.NotificationResponse;
import com.womensecurity.backend.repository.EmergencyContactRepository;
import com.womensecurity.backend.repository.LocationRepository;
import com.womensecurity.backend.repository.NotificationLogRepository;
import com.womensecurity.backend.repository.ThreatRepository;

@Service
public class EmergencyNotificationService {

    @Autowired
    private EmergencyContactRepository emergencyContactRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ThreatRepository threatRepository;

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    public NotificationResponse sendNotification(NotificationRequest request) {

        NotificationResponse response = new NotificationResponse();

        Long userId = request.getUserId();

        // -------------------------
        // Latest Location
        // -------------------------

        Optional<Location> locationOptional =
                locationRepository.findFirstByUserIdOrderByUpdatedAtDesc(userId);

        if (locationOptional.isEmpty()) {

            response.setStatus("FAILED");
            response.setMessage("Location not found");

            return response;
        }

        Location location = locationOptional.get();

        response.setLocation(
                location.getLatitude() + ", " +
                location.getLongitude());

        // -------------------------
        // Latest Threat
        // -------------------------
            List<ThreatDetection> threats = threatRepository.findByUserId(userId);

Optional<ThreatDetection> threatOptional;

if (threats.isEmpty()) {
    threatOptional = Optional.empty();
} else {
    threatOptional = Optional.of(threats.get(threats.size() - 1));
}
        

        String alertMessage;

        if (threatOptional.isPresent()) {

            ThreatDetection threat = threatOptional.get();

            if ("HIGH".equalsIgnoreCase(threat.getRiskLevel())) {

                alertMessage =
                        "HIGH RISK! SOS Activated. Please reach me immediately.";

            } else if ("MEDIUM".equalsIgnoreCase(threat.getRiskLevel())) {

                alertMessage =
                        "I feel unsafe. Please monitor my location.";

            } else {

                alertMessage =
                        "I am safe.";

            }

        } else {

            alertMessage =
                    "Threat information unavailable.";

        }

        response.setMessage(alertMessage);

        // -------------------------
        // Emergency Contacts
        // -------------------------

        List<EmergencyContact> contacts =
                emergencyContactRepository.findByUserId(userId);

        List<String> contactList = new ArrayList<>();

        for (EmergencyContact contact : contacts) {

            contactList.add(

                    contact.getContactName()

                    + " - "

                    + contact.getContactNumber()

            );

        }

        response.setContacts(contactList);

        response.setStatus("READY TO SEND");

        // -------------------------
        // Save Notification Log
        // -------------------------

        NotificationLog log = new NotificationLog();

        log.setUserId(userId);

        log.setMessage(alertMessage);

        log.setStatus("READY");

        log.setCreatedAt(LocalDateTime.now());

        notificationLogRepository.save(log);

        return response;

    }

}