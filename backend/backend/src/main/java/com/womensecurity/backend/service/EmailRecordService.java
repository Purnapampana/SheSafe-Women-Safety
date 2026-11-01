package com.womensecurity.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.womensecurity.backend.entity.EmailRecord;
import com.womensecurity.backend.repository.EmailRecordRepository;

@Service
public class EmailRecordService {

    private final EmailRecordRepository emailRecordRepository;

    public EmailRecordService(
            EmailRecordRepository emailRecordRepository) {

        this.emailRecordRepository = emailRecordRepository;
    }

    public EmailRecord saveEmailRecord(EmailRecord emailRecord) {

        if (emailRecord.getIncidentId() == null) {
            throw new IllegalArgumentException("Incident ID is required");
        }

        if (emailRecord.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        return emailRecordRepository.save(emailRecord);
    }

    public List<EmailRecord> getEmailsByIncidentId(Long incidentId) {

        return emailRecordRepository
                .findByIncidentIdOrderBySentAtAsc(incidentId);
    }

    public List<EmailRecord> getEmailsByUserId(Long userId) {

        return emailRecordRepository.findByUserId(userId);
    }
}