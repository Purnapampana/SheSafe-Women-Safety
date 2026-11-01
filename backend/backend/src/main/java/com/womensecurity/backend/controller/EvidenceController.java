package com.womensecurity.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.womensecurity.backend.entity.Evidence;
import com.womensecurity.backend.service.EvidenceService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/evidence")
@CrossOrigin("*")
public class EvidenceController {

    private final EvidenceService evidenceService;


    public EvidenceController(
            EvidenceService evidenceService) {

        this.evidenceService =
                evidenceService;
    }


    // =====================================================
    // UPLOAD AUDIO / VIDEO
    // =====================================================

   // =====================================================
// UPLOAD AUDIO / VIDEO
// =====================================================

@PostMapping("/upload")
public ResponseEntity<?> uploadEvidence(

        @RequestParam("file")
        MultipartFile file,

        @RequestParam("userId")
        Long userId,

        // ⭐ NEW
        @RequestParam("incidentId")
        Long incidentId,

        @RequestParam("fileType")
        String fileType,

        @RequestParam(
        value = "recordedAt",
        required = false)
String recordedAt,

@RequestParam(
        value = "duration",
        required = false)
Integer duration)
        {


    try {

        System.out.println(
                "Uploading evidence:"
        );

        System.out.println(
                "User ID: " + userId
        );

        System.out.println(
                "Incident ID: " + incidentId
        );

        System.out.println(
                "File Type: " + fileType
        );
        System.out.println(
        "Duration: " + duration + " seconds"
);


        Evidence evidence =
                evidenceService.uploadEvidence(
                        file,
                        userId,
                        incidentId,   // ⭐ NEW
                        fileType,
                        recordedAt, duration
                );


        return ResponseEntity.ok(
                evidence
        );


    } catch (Exception e) {

        e.printStackTrace();

        return ResponseEntity
                .badRequest()
                .body(
                        "Evidence upload failed: "
                        + e.getMessage()
                );
    }
}

    // =====================================================
    // GET USER EVIDENCE
    // =====================================================

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Evidence>>
            getUserEvidence(
                    @PathVariable Long userId) {

        return ResponseEntity.ok(
                evidenceService
                        .getUserEvidence(userId)
        );
    }
    // =====================================================
// VIEW EVIDENCE FILE
// =====================================================

@GetMapping("/file/{id}")
public ResponseEntity<Resource> getEvidenceFile(
        @PathVariable Long id) {

    try {

        Evidence evidence =
                evidenceService
                        .getEvidenceById(id);


        Path path =
                Paths.get(
                        evidence.getFilePath()
                );


        Resource resource =
                new UrlResource(
                        path.toUri()
                );


        if (!resource.exists()) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        String contentType =
                Files.probeContentType(path);


        if (contentType == null) {

            if (evidence.getFileType()
                    .equalsIgnoreCase("audio")) {

                contentType =
                        "audio/webm";

            } else {

                contentType =
                        "video/webm";
            }
        }


        return ResponseEntity.ok()
                .contentType(
                        MediaType.parseMediaType(
                                contentType
                        )
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" +
                        evidence.getFileName() +
                        "\""
                )
                .body(resource);


    } catch (Exception e) {

        return ResponseEntity
                .notFound()
                .build();
    }
}
@DeleteMapping("/delete/{id}")
public String deleteEvidence(@PathVariable Long id) {
    return evidenceService.deleteEvidence(id);
}
}