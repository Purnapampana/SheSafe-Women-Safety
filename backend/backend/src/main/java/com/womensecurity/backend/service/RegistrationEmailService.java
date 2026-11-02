
package com.womensecurity.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationEmailService {

    @Autowired
    private BrevoEmailService brevoEmailService;

    public void sendRegistrationOtp(
            String email,
            String fullName,
            String otp) {

        String content =
                "Hello " + fullName + ",\n\n"
                + "Welcome to SheSafe.\n\n"
                + "Your registration OTP is:\n\n"
                + otp + "\n\n"
                + "This OTP is valid for 5 minutes.\n\n"
                + "Please do not share this OTP with anyone.\n\n"
                + "If you did not create a SheSafe account, "
                + "please ignore this email.\n\n"
                + "Regards,\nSheSafe Team";

        brevoEmailService.sendEmail(
                email,
                fullName,
                "SheSafe - Registration OTP",
                content
        );
    }
}
