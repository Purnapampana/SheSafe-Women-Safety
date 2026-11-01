package com.womensecurity.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.womensecurity.backend.entity.CallRecord;
import com.womensecurity.backend.repository.CallRecordRepository;

@Service
public class CallRecordService {

    private final CallRecordRepository callRecordRepository;

    public CallRecordService(CallRecordRepository callRecordRepository) {
        this.callRecordRepository = callRecordRepository;
    }

    public CallRecord saveCallRecord(CallRecord callRecord) {

        if (callRecord.getIncidentId() == null) {
            throw new IllegalArgumentException("Incident ID is required");
        }

        if (callRecord.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        return callRecordRepository.save(callRecord);
    }

    public List<CallRecord> getCallsByIncidentId(Long incidentId) {
        return callRecordRepository.findByIncidentIdOrderByCallTimeAsc(incidentId);
    }

    public List<CallRecord> getCallsByUserId(Long userId) {
        return callRecordRepository.findByUserId(userId);
    }
}