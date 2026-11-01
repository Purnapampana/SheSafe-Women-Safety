package com.womensecurity.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.womensecurity.backend.entity.MediaRecord;
import com.womensecurity.backend.repository.MediaRecordRepository;

@RestController
@RequestMapping("/api/media")
@CrossOrigin("*")
public class MediaRecordController {

    private final MediaRecordRepository mediaRecordRepository;

    public MediaRecordController(
            MediaRecordRepository mediaRecordRepository) {

        this.mediaRecordRepository =
                mediaRecordRepository;
    }


    // =====================================================
    // GET MEDIA RECORDS BY USER
    // =====================================================

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MediaRecord>> getUserMedia(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                mediaRecordRepository.findByUserId(userId)
        );
    }


    // =====================================================
    // GET MEDIA RECORDS BY INCIDENT
    // =====================================================

    @GetMapping("/incident/{incidentId}")
    public ResponseEntity<List<MediaRecord>> getIncidentMedia(
            @PathVariable Long incidentId) {

        return ResponseEntity.ok(
                mediaRecordRepository
                        .findByIncidentIdOrderByRecordedAtAsc(
                                incidentId
                        )
        );
    }
}