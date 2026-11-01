package com.womensecurity.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.womensecurity.backend.dto.NotificationRequest;
import com.womensecurity.backend.model.NotificationResponse;
import com.womensecurity.backend.service.EmergencyNotificationService;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin("*")
public class EmergencyNotificationController {

    @Autowired
    private EmergencyNotificationService emergencyNotificationService;

    @PostMapping("/send")
    public NotificationResponse sendNotification(
            @RequestBody NotificationRequest request) {

        return emergencyNotificationService.sendNotification(request);

    }

}