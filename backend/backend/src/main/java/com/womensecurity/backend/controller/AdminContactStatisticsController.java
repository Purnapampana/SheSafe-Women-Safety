package com.womensecurity.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.womensecurity.backend.repository.EmailRecordRepository;
import com.womensecurity.backend.repository.CallRecordRepository;
import com.womensecurity.backend.repository.EmergencyContactRepository;
import com.womensecurity.backend.repository.MessageRecordRepository;

@RestController
@RequestMapping("/api/admin/contact-statistics")
@CrossOrigin("*")
public class AdminContactStatisticsController {

    @Autowired
    private EmergencyContactRepository emergencyContactRepository;

    @Autowired
    private CallRecordRepository callRecordRepository;

    @Autowired
    private MessageRecordRepository messageRecordRepository;
     @Autowired
    private EmailRecordRepository emailRecordRepository;

    @GetMapping
    public ContactStatisticsResponse getStatistics() {

        long totalContacts =
                emergencyContactRepository.count();

        long totalCalls =
                callRecordRepository.count();

        long totalMessages =
                messageRecordRepository.count();
                long totalEmails = emailRecordRepository.count();

        return new ContactStatisticsResponse(
                totalContacts,
                totalCalls,
                totalMessages,totalEmails
        );
    }

    public static class ContactStatisticsResponse {

        private long totalContacts;
        private long totalCalls;
        private long totalMessages;
        private long totalEmails;

        public ContactStatisticsResponse(
                long totalContacts,
                long totalCalls,
                long totalMessages,long totalEmails) {

            this.totalContacts = totalContacts;
            this.totalCalls = totalCalls;
            this.totalMessages = totalMessages;
            this.totalEmails = totalEmails;
        }

        public long getTotalContacts() {
            return totalContacts;
        }

        public long getTotalCalls() {
            return totalCalls;
        }

        public long getTotalMessages() {
            return totalMessages;
        }
        public long getTotalEmails() {
            return totalEmails;
        }
    }
}