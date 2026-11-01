package com.womensecurity.backend.controller;

import com.womensecurity.backend.entity.MessageRecord;
import com.womensecurity.backend.service.MessageRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageRecordController {

    private final MessageRecordService messageRecordService;

    public MessageRecordController(MessageRecordService messageRecordService) {
        this.messageRecordService = messageRecordService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveMessage(
            @RequestBody MessageRecord messageRecord) {

        try {
            MessageRecord saved =
                    messageRecordService.saveMessageRecord(messageRecord);

            return ResponseEntity.ok(saved);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to save message record: " + e.getMessage());
        }
    }

    @GetMapping("/incident/{incidentId}")
    public ResponseEntity<List<MessageRecord>> getMessagesByIncident(
            @PathVariable Long incidentId) {

        return ResponseEntity.ok(
                messageRecordService.getMessagesByIncidentId(incidentId)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MessageRecord>> getMessagesByUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                messageRecordService.getMessagesByUserId(userId)
        );
    }
}