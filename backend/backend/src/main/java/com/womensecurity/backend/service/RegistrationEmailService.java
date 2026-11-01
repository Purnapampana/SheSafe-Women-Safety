package com.womensecurity.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class RegistrationEmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendRegistrationOtp(
            String email,
            String fullName,
            String otp) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(email);

        message.setSubject(
                "SheSafe - Registration OTP"
        );

        message.setText(
                "Hello " + fullName + ",\n\n" +

                "Welcome to SheSafe.\n\n" +

                "Your registration OTP is:\n\n" +

                otp + "\n\n" +

                "This OTP is valid for 5 minutes.\n\n" +

                "Please do not share this OTP with anyone.\n\n" +

                "If you did not create a SheSafe account, " +
                "please ignore this email.\n\n" +

                "Regards,\n" +
                "SheSafe Team"
        );

        mailSender.send(message);
    }
}