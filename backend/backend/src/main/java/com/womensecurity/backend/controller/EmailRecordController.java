package com.womensecurity.backend.controller;

import com.womensecurity.backend.entity.EmailRecord;
import com.womensecurity.backend.service.EmailRecordService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emails")
@CrossOrigin(origins = "*")
public class EmailRecordController {

    private final EmailRecordService emailRecordService;

    public EmailRecordController(
            EmailRecordService emailRecordService) {

        this.emailRecordService = emailRecordService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveEmail(
            @RequestBody EmailRecord emailRecord) {

        try {

            EmailRecord saved =
                    emailRecordService.saveEmailRecord(emailRecord);

            return ResponseEntity.ok(saved);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body("Failed to save email record: "
                            + e.getMessage());
        }
    }

    @GetMapping("/incident/{incidentId}")
    public ResponseEntity<List<EmailRecord>> getEmailsByIncident(
            @PathVariable Long incidentId) {

        return ResponseEntity.ok(
                emailRecordService
                        .getEmailsByIncidentId(incidentId)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EmailRecord>> getEmailsByUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                emailRecordService
                        .getEmailsByUserId(userId)
        );
    }
}