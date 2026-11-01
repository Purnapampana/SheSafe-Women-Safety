package com.womensecurity.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.womensecurity.backend.entity.MessageRecord;
import com.womensecurity.backend.repository.MessageRecordRepository;

@Service
public class MessageRecordService {

    private final MessageRecordRepository messageRecordRepository;

    public MessageRecordService(MessageRecordRepository messageRecordRepository) {
        this.messageRecordRepository = messageRecordRepository;
    }

    public MessageRecord saveMessageRecord(MessageRecord messageRecord) {

        if (messageRecord.getIncidentId() == null) {
            throw new IllegalArgumentException("Incident ID is required");
        }

        if (messageRecord.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        return messageRecordRepository.save(messageRecord);
    }

    public List<MessageRecord> getMessagesByIncidentId(Long incidentId) {
        return messageRecordRepository.findByIncidentIdOrderBySentAtAsc(incidentId);
    }

    public List<MessageRecord> getMessagesByUserId(Long userId) {
        return messageRecordRepository.findByUserId(userId);
    }
}