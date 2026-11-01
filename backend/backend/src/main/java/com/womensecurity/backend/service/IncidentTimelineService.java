package com.womensecurity.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.womensecurity.backend.dto.IncidentTimelineResponse;
import com.womensecurity.backend.dto.IncidentTimelineResponse.TimelineEvent;
import com.womensecurity.backend.entity.CallRecord;
import com.womensecurity.backend.entity.IncidentTimeline;
import com.womensecurity.backend.entity.Location;
import com.womensecurity.backend.entity.MediaRecord;
import com.womensecurity.backend.entity.MessageRecord;
import com.womensecurity.backend.repository.CallRecordRepository;
import com.womensecurity.backend.repository.IncidentTimelineRepository;
import com.womensecurity.backend.repository.LocationRepository;
import com.womensecurity.backend.repository.MediaRecordRepository;
import com.womensecurity.backend.repository.MessageRecordRepository;

@Service
public class IncidentTimelineService {

    @Autowired
    private IncidentTimelineRepository incidentTimelineRepository;

    @Autowired
    private CallRecordRepository callRecordRepository;

    @Autowired
    private MessageRecordRepository messageRecordRepository;

    @Autowired
    private MediaRecordRepository mediaRecordRepository;

    @Autowired
    private LocationRepository locationRepository;
 public List<IncidentTimeline> getIncidentsByUserId(Long userId) {

    return incidentTimelineRepository
            .findByUserIdOrderBySosActivationTimeDesc(userId);
}

    // ==========================================
    // CREATE INCIDENT
    // ==========================================

   // ==========================================
// CREATE INCIDENT
// ==========================================

public IncidentTimeline createIncident(Long userId) {

    // ==========================================
    // RESOLVE PREVIOUS ACTIVE INCIDENTS
    // ==========================================

    List<IncidentTimeline> previousIncidents =
            incidentTimelineRepository
                    .findByUserId(userId);


    for (IncidentTimeline previous :
            previousIncidents) {

        if ("ACTIVE".equalsIgnoreCase(
                previous.getEmergencyResponseStatus())) {

            previous.setEmergencyResponseStatus(
                    "RESOLVED");

            previous.setResolvedAt(
                    LocalDateTime.now());

            incidentTimelineRepository.save(
                    previous
            );
        }
    }


    // ==========================================
    // GET NEXT INCIDENT NUMBER FOR THIS USER
    // ==========================================

    IncidentTimeline latestIncident =
            incidentTimelineRepository
                    .findTopByUserIdOrderByIncidentNumberDesc(
                            userId);


    Long nextIncidentNumber = 1L;


    if (latestIncident != null
            && latestIncident.getIncidentNumber() != null) {

        nextIncidentNumber =
                latestIncident.getIncidentNumber() + 1;
    }


    // ==========================================
    // CREATE NEW INCIDENT
    // ==========================================

    IncidentTimeline incident =
            new IncidentTimeline();


    incident.setUserId(userId);


    // User-specific number
    incident.setIncidentNumber(
            nextIncidentNumber
    );


    incident.setSosActivationTime(
            LocalDateTime.now());


    incident.setEmergencyResponseStatus(
            "ACTIVE");


    // Database ID is still generated automatically
    return incidentTimelineRepository.save(
            incident
    );
}
    // ==========================================
    // GET INCIDENT
    // ==========================================

    public IncidentTimeline getIncident(Long incidentId) {

        return incidentTimelineRepository
                .findById(incidentId)
                .orElse(null);
    }

    // ==========================================
    // UPDATE RESPONSE STATUS
    // ==========================================

    public IncidentTimeline updateResponseStatus(
            Long incidentId,
            String status) {

        IncidentTimeline incident =
                incidentTimelineRepository
                        .findById(incidentId)
                        .orElse(null);

        if (incident == null) {
            return null;
        }

        incident.setEmergencyResponseStatus(status);

        if ("RESOLVED".equalsIgnoreCase(status)) {

            incident.setResolvedAt(
                    LocalDateTime.now());
        }

        return incidentTimelineRepository.save(incident);
    }

    // ==========================================
    // GET COMPLETE TIMELINE
    // ==========================================

    public IncidentTimelineResponse
            getCompleteTimeline(Long incidentId) {

        IncidentTimeline incident =
                incidentTimelineRepository
                        .findById(incidentId)
                        .orElse(null);

        if (incident == null) {
            return null;
        }

        IncidentTimelineResponse response =
                new IncidentTimelineResponse();

        response.setIncidentId(incident.getId());
        response.setIncidentNumber(
        incident.getIncidentNumber()
);


        response.setUserId(incident.getUserId());

        response.setSosActivationTime(
                incident.getSosActivationTime());

        response.setEmergencyResponseStatus(
                incident.getEmergencyResponseStatus());

        response.setResolvedAt(
                incident.getResolvedAt());

        List<TimelineEvent> events =
                new ArrayList<>();

        // ======================================
        // SOS EVENT
        // ======================================

        events.add(
                new TimelineEvent(
                        "SOS_ACTIVATED",
                        incident.getSosActivationTime(),
                        "SOS activated"));

  // ======================================
// GPS HISTORY
// ======================================

List<Location> locations =
        locationRepository.findByIncidentId(
                incidentId);


for (Location location : locations) {

    TimelineEvent event =
            new TimelineEvent(
                    "LOCATION",
                    location.getUpdatedAt(),
                    "Emergency location recorded");

    event.setLatitude(
            location.getLatitude());

    event.setLongitude(
            location.getLongitude());

    event.setAccuracy(
            location.getAccuracy());

    events.add(event);
}

        // ======================================
        // CALL HISTORY
        // ======================================

        // ======================================
// CALL HISTORY
// ======================================

List<CallRecord> calls =
        callRecordRepository
                .findByIncidentId(incidentId);

for (CallRecord call : calls) {

    String contact;

    if (call.getContactName() != null
            && !call.getContactName().trim().isEmpty()) {

        contact = call.getContactName();

    } else {

        contact = call.getPhoneNumber();
    }

    String description =
            "Call to " + contact
            + "\nStatus: " + call.getCallStatus()
            + "\nDuration: "
            + (call.getDurationSeconds() != null
                ? call.getDurationSeconds()
                : 0)
            + " seconds";

    events.add(
            new TimelineEvent(
                    "CALL",
                    call.getCallTime(),
                    description));
}

        // ======================================
        // MESSAGE HISTORY
        // ======================================

        // ======================================
// MESSAGE HISTORY
// ======================================

List<MessageRecord> messages =
        messageRecordRepository
                .findByIncidentId(incidentId);

for (MessageRecord message : messages) {

    String recipient;

    if (message.getRecipientName() != null
            && !message.getRecipientName().trim().isEmpty()) {

        recipient = message.getRecipientName();

    } else {

        recipient = message.getRecipientPhone();
    }

    String description =
            "Message sent to " + recipient
            + "\nStatus: "
            + message.getDeliveryStatus();

    events.add(
            new TimelineEvent(
                    "MESSAGE",
                    message.getSentAt(),
                    description));
}
        // ======================================
// MEDIA HISTORY
// ======================================

List<MediaRecord> mediaRecords =
        mediaRecordRepository
                .findByIncidentId(incidentId);


for (MediaRecord media : mediaRecords) {

    TimelineEvent event =
            new TimelineEvent(
                    "MEDIA",
                    media.getRecordedAt(),
                    media.getMediaType()
                    + " recording");

    event.setFileName(
            media.getFileName());

    event.setEvidenceId(
            media.getEvidenceId());

    event.setDurationSeconds(
            media.getDurationSeconds());

    events.add(event);
}

        // ======================================
// EMERGENCY RESPONSE
// ======================================

if (incident.getEmergencyResponseStatus()
        != null) {

    LocalDateTime responseTime =
            incident.getResolvedAt();

    if (responseTime == null) {

        responseTime =
                incident.getSosActivationTime();
    }

    events.add(
            new TimelineEvent(
                    "EMERGENCY_RESPONSE",
                    responseTime,
                    "Emergency response status: "
                    + incident
                        .getEmergencyResponseStatus()
            )
    );
}

        // ======================================
        // SORT CHRONOLOGICALLY
        // ======================================

        events.sort(
                Comparator.comparing(
                        TimelineEvent::getTime,
                        Comparator.nullsLast(
                                Comparator.naturalOrder())));

        response.setTimeline(events);

        return response;
    }
}