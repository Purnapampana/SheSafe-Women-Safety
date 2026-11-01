package com.womensecurity.backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.womensecurity.backend.entity.Evidence;
import com.womensecurity.backend.entity.MediaRecord;
import com.womensecurity.backend.repository.EvidenceRepository;
import com.womensecurity.backend.repository.MediaRecordRepository;


@Service
public class EvidenceService {

    private final EvidenceRepository evidenceRepository;
    private final MediaRecordRepository mediaRecordRepository;

    private final Path uploadDirectory =
            Paths.get("uploads/evidence");

   public EvidenceService(
        EvidenceRepository evidenceRepository,
        MediaRecordRepository mediaRecordRepository) {

    this.evidenceRepository =
            evidenceRepository;

    this.mediaRecordRepository =
            mediaRecordRepository;

        try {

            Files.createDirectories(
                    uploadDirectory
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to create evidence directory",
                    e
            );
        }
    }


    // =====================================================
    // UPLOAD EVIDENCE
    // =====================================================

    public Evidence uploadEvidence(
            MultipartFile file,
            Long userId,
             Long incidentId,
            String fileType,
            String recordedAt,Integer duration)
            throws Exception {


        if (file == null || file.isEmpty()) {

            throw new RuntimeException(
                    "Evidence file is empty"
            );
        }


        if (!fileType.equalsIgnoreCase("audio")
                && !fileType.equalsIgnoreCase("video")) {

            throw new RuntimeException(
                    "Only audio and video files are allowed"
            );
        }


        // Generate unique filename

        String originalName =
                file.getOriginalFilename();

        if (originalName == null) {

            originalName = "evidence.webm";
        }


        String extension = ".webm";

        String fileName =
                System.currentTimeMillis()
                + "_"
                + fileType
                + extension;


        // Complete file path

        Path filePath =
                uploadDirectory.resolve(fileName);


        // Save physical file

        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );


        // Generate SHA-256 hash

        String hash =
                generateSHA256(filePath);


        // Create database record

        Evidence evidence =
                new Evidence();
        if (incidentId == null) {
    throw new RuntimeException(
            "Incident ID is required"
    );
}

        evidence.setUserId(userId);

        evidence.setFileName(
                fileName
        );

        evidence.setFileType(
                fileType
        );

        evidence.setFilePath(
                filePath.toString()
        );

        evidence.setFileHash(
                hash
        );


        // Recording timestamp

        if (recordedAt != null
                && !recordedAt.isBlank()) {

            try {

                evidence.setRecordedAt(
                        LocalDateTime.parse(
                                recordedAt
                        )
                );

            } catch (Exception e) {

                evidence.setRecordedAt(
                        LocalDateTime.now()
                );
            }

        } else {

            evidence.setRecordedAt(
                    LocalDateTime.now()
            );
        }


        evidence.setUploadedAt(
                LocalDateTime.now()
        );


       Evidence savedEvidence =
        evidenceRepository.save(
                evidence
        );

MediaRecord mediaRecord =
        new MediaRecord();

mediaRecord.setIncidentId(
        incidentId
);

mediaRecord.setUserId(
        userId
);

mediaRecord.setMediaType(
        fileType
);

mediaRecord.setFileName(
        fileName
);

mediaRecord.setEvidenceId(
        savedEvidence.getId()
);

mediaRecord.setFileSize(
        file.getSize()
);

mediaRecord.setDurationSeconds(
        duration
);

mediaRecord.setRecordedAt(
        savedEvidence.getRecordedAt()
);

mediaRecordRepository.save(
        mediaRecord
);

return savedEvidence;
    }


    // =====================================================
    // SHA-256 HASH
    // =====================================================

    private String generateSHA256(
            Path filePath)
            throws Exception {


        byte[] fileBytes =
                Files.readAllBytes(filePath);


        MessageDigest digest =
                MessageDigest.getInstance(
                        "SHA-256"
                );


        byte[] hashBytes =
                digest.digest(fileBytes);


        StringBuilder result =
                new StringBuilder();


        for (byte b : hashBytes) {

            result.append(
                    String.format(
                            "%02x",
                            b
                    )
            );
        }


        return result.toString();
    }


    // =====================================================
    // GET USER EVIDENCE
    // =====================================================

    public List<Evidence> getUserEvidence(
            Long userId) {

        return evidenceRepository
                .findByUserId(userId);
    }
    // =====================================================
// GET EVIDENCE BY ID
// =====================================================

public Evidence getEvidenceById(Long id) {

    return evidenceRepository
            .findById(id)
            .orElseThrow(
                () -> new RuntimeException(
                    "Evidence not found"
                )
            );
}
public String deleteEvidence(Long id) {

    Optional<Evidence> optionalEvidence =
            evidenceRepository.findById(id);

    if (optionalEvidence.isEmpty()) {
        return "Evidence not found";
    }

    Evidence evidence = optionalEvidence.get();

    try {

        // Delete physical file
        Path path = Paths.get(evidence.getFilePath());

        Files.deleteIfExists(path);

        // Delete database record
        evidenceRepository.deleteById(id);

        return "Evidence deleted successfully";

    } catch (Exception e) {

        return "Failed to delete evidence: "
                + e.getMessage();
    }
}
}