package com.womensecurity.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.womensecurity.backend.dto.EmergencyResponseRequest;
import com.womensecurity.backend.dto.NotificationRequest;
import com.womensecurity.backend.dto.SOSRequest;
import com.womensecurity.backend.dto.WorkflowRequest;
import com.womensecurity.backend.entity.EmergencyWorkflowLog;
import com.womensecurity.backend.entity.Location;
import com.womensecurity.backend.model.EmergencyResponse;
import com.womensecurity.backend.model.NotificationResponse;
import com.womensecurity.backend.model.WorkflowResponse;
import com.womensecurity.backend.repository.EmergencyWorkflowLogRepository;
import com.womensecurity.backend.repository.LocationRepository;

@Service
public class EmergencyWorkflowService {

    @Autowired
    private SOSService sosService;

    @Autowired
    private EmergencyResponseService emergencyResponseService;

    @Autowired
    private EmergencyNotificationService emergencyNotificationService;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private EmergencyWorkflowLogRepository workflowLogRepository;

    public WorkflowResponse activateWorkflow(WorkflowRequest request) {

        WorkflowResponse response = new WorkflowResponse();

        Long userId = request.getUserId();

        EmergencyWorkflowLog log = new EmergencyWorkflowLog();

        log.setUserId(userId);

        log.setStartedAt(LocalDateTime.now());

        Location location =
                locationRepository
                        .findFirstByUserIdOrderByUpdatedAtDesc(userId)
                        .orElse(null);

        if (location == null) {

            response.setWorkflowStatus("FAILED");

            log.setWorkflowStatus("FAILED");

            log.setCompletedAt(LocalDateTime.now());

            workflowLogRepository.save(log);

            return response;
        }

        //----------------------------------------------------
        // Activate SOS
        //----------------------------------------------------

        SOSRequest sosRequest = new SOSRequest();

        sosRequest.setUserId(userId);

        sosRequest.setLatitude(location.getLatitude());

        sosRequest.setLongitude(location.getLongitude());

        sosRequest.setStatus("ACTIVE");

        String sosStatus =
                sosService.activateSOS(sosRequest);

        response.setSosStatus(sosStatus);

        //----------------------------------------------------
        // Emergency Response
        //----------------------------------------------------

        EmergencyResponseRequest emergencyRequest =
                new EmergencyResponseRequest();

        emergencyRequest.setUserId(userId);

        EmergencyResponse emergencyResponse =
                emergencyResponseService.generateResponse(
                        emergencyRequest);

        response.setThreatLevel(
                emergencyResponse.getThreatLevel());

        response.setDangerScore(
                emergencyResponse.getDangerScore());

        response.setNearestPoliceStation(
                emergencyResponse.getNearestPoliceStation());

        response.setNearestHospital(
                emergencyResponse.getNearestHospital());

        //----------------------------------------------------
        // Notification
        //----------------------------------------------------

        NotificationRequest notificationRequest =
                new NotificationRequest();

        notificationRequest.setUserId(userId);

        NotificationResponse notificationResponse =
                emergencyNotificationService
                        .sendNotification(notificationRequest);

        response.setNotificationStatus(
                notificationResponse.getStatus());

        response.setContacts(
                new ArrayList<>(notificationResponse.getContacts()));

        //----------------------------------------------------
        // Save Workflow Log
        //----------------------------------------------------

        response.setWorkflowStatus("COMPLETED");

        log.setWorkflowStatus("COMPLETED");

        log.setCompletedAt(LocalDateTime.now());

        workflowLogRepository.save(log);

        return response;

    }

}