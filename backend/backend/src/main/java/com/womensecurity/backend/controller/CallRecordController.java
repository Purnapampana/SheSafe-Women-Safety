package com.womensecurity.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.womensecurity.backend.entity.CallRecord;
import com.womensecurity.backend.service.CallRecordService;

@RestController
@RequestMapping("/api/calls")
@CrossOrigin(origins = "*")
public class CallRecordController {

    private final CallRecordService callRecordService;

    public CallRecordController(CallRecordService callRecordService) {
        this.callRecordService = callRecordService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveCall(@RequestBody CallRecord callRecord) {

        try {
            CallRecord saved = callRecordService.saveCallRecord(callRecord);
            return ResponseEntity.ok(saved);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to save call record: " + e.getMessage());
        }
    }

    @GetMapping("/incident/{incidentId}")
    public ResponseEntity<List<CallRecord>> getCallsByIncident(
            @PathVariable Long incidentId) {

        return ResponseEntity.ok(
                callRecordService.getCallsByIncidentId(incidentId)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CallRecord>> getCallsByUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                callRecordService.getCallsByUserId(userId)
        );
    }
}