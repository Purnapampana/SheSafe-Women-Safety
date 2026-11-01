package com.womensecurity.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.womensecurity.backend.entity.EmergencyContact;
import com.womensecurity.backend.entity.EmailRecord;
import com.womensecurity.backend.entity.User;
import com.womensecurity.backend.repository.EmailRecordRepository;
import com.womensecurity.backend.repository.EmergencyContactRepository;
import com.womensecurity.backend.repository.IncidentReportRepository;
import com.womensecurity.backend.repository.UserRepository;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmergencyContactRepository emergencyContactRepository;

    @Autowired
private EmailRecordRepository emailRecordRepository;

    public void sendSOSAlert(
            Long userId,
            Long incidentId,
            Double latitude,
            Double longitude) {

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        List<EmergencyContact> contacts =
                emergencyContactRepository
                        .findByUserId(userId);

        String mapLink =
                "https://www.google.com/maps?q="
                        + latitude
                        + ","
                        + longitude;

        String message =
                "🚨 SHE SAFE SOS ALERT\n\n"
                + "An emergency SOS has been activated.\n\n"
                + "User: "
                + user.getFullName()
                + "\n"
                 + "PhoneNumber: "
                + user.getPhoneNumber()
                + "\n"
                + "Time: "
                + java.time.LocalDateTime.now()
                + "\n\n"
                + "Emergency Location:\n"
                + mapLink
                + "\n\n"
                + "Please contact the user immediately.\n\n";

        for (EmergencyContact contact : contacts) {

            if (contact.getContactEmail() == null ||
                    contact.getContactEmail()
                            .trim()
                            .isEmpty()) {

                continue;
            }

            SimpleMailMessage email =
                    new SimpleMailMessage();

            email.setTo(
                    contact.getContactEmail()
            );

            email.setSubject(
                    "🚨 SHE SAFE SOS ALERT - Emergency"
            );

            email.setText(message);

            mailSender.send(email);
            EmailRecord emailRecord = new EmailRecord();

emailRecord.setIncidentId(incidentId);
emailRecord.setUserId(userId);
emailRecord.setRecipientName(contact.getContactName());
emailRecord.setRecipientEmail(contact.getContactEmail());
emailRecord.setMessage(message);
emailRecord.setDeliveryStatus("SENT");
emailRecord.setSentAt(java.time.LocalDateTime.now());

emailRecordRepository.save(emailRecord);
        }
    }
}