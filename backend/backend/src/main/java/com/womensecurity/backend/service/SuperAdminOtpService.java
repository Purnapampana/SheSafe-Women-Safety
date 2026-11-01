package com.womensecurity.backend.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SuperAdminOtpService {

    @Autowired
    private JavaMailSender mailSender;

    private final Map<String, OtpData> otpStorage =
            new ConcurrentHashMap<>();


    // =====================================================
    // GENERATE AND SEND OTP
    // =====================================================

    public void generateAndSendOtp(String email) {

        sendNewOtp(email);
    }


    // =====================================================
    // RESEND OTP
    // =====================================================

    public void resendOtp(String email) {

        sendNewOtp(email);
    }


    // =====================================================
    // COMMON OTP GENERATION + EMAIL METHOD
    // =====================================================

    private void sendNewOtp(String email) {

        String normalizedEmail =
                email.trim().toLowerCase();


        // Generate new 6-digit OTP
        String otp =
                String.format(
                        "%06d",
                        new Random().nextInt(1000000)
                );


        // OTP valid for 5 minutes
        LocalDateTime expiry =
                LocalDateTime.now().plusMinutes(5);


        // Store new OTP
        //
        // If an old OTP exists for this email,
        // it will automatically be replaced.
        otpStorage.put(
                normalizedEmail,
                new OtpData(
                        otp,
                        expiry
                )
        );


        // Create email
        SimpleMailMessage message =
                new SimpleMailMessage();


        message.setTo(normalizedEmail);

        message.setSubject(
                "SHE SAFE - Super Admin Login OTP"
        );


        message.setText(
                "Your Super Admin login OTP is: "
                + otp
                + "\n\n"
                + "This OTP is valid for 5 minutes."
                + "\n"
                + "If you requested a new OTP, "
                + "the previous OTP is no longer valid."
        );


        // Send email
        mailSender.send(message);
    }


    // =====================================================
    // VERIFY OTP
    // =====================================================

    public boolean verifyOtp(
            String email,
            String otp) {

        String normalizedEmail =
                email.trim().toLowerCase();


        OtpData data =
                otpStorage.get(normalizedEmail);


        // No OTP found
        if (data == null) {

            return false;
        }


        // OTP expired
        if (LocalDateTime.now()
                .isAfter(data.expiry)) {

            otpStorage.remove(
                    normalizedEmail
            );

            return false;
        }


        // OTP does not match
        if (!data.otp.equals(otp)) {

            return false;
        }


        // OTP is correct
        //
        // Remove it so it cannot be reused.
        otpStorage.remove(
                normalizedEmail
        );


        return true;
    }


    // =====================================================
    // OTP DATA
    // =====================================================

    private static class OtpData {

        private String otp;

        private LocalDateTime expiry;


        public OtpData(
                String otp,
                LocalDateTime expiry) {

            this.otp = otp;
            this.expiry = expiry;
        }
    }
}

