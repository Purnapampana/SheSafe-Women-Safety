
package com.womensecurity.backend.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SuperAdminOtpService {

    @Autowired
    private BrevoEmailService brevoEmailService;

    private final Map<String, OtpData> otpStorage =
            new ConcurrentHashMap<>();

    // GENERATE AND SEND OTP

    public void generateAndSendOtp(String email) {
        sendNewOtp(email);
    }

    // RESEND OTP

    public void resendOtp(String email) {
        sendNewOtp(email);
    }

    // COMMON OTP GENERATION + EMAIL METHOD

    private void sendNewOtp(String email) {

        String normalizedEmail =
                email.trim().toLowerCase();

        // Generate new 6-digit OTP
        String otp = String.format(
                "%06d",
                new SecureRandom().nextInt(1000000)
        );

        // OTP valid for 5 minutes
        LocalDateTime expiry =
                LocalDateTime.now().plusMinutes(5);

        // Store new OTP
        otpStorage.put(
                normalizedEmail,
                new OtpData(otp, expiry)
        );

        // Email content
        String content =
                "Your Super Admin login OTP is: "
                + otp
                + "\n\n"
                + "This OTP is valid for 5 minutes."
                + "\n"
                + "If you requested a new OTP, "
                + "the previous OTP is no longer valid."
                + "\n\n"
                + "SheSafe Security Team";

        // Send email using Brevo
        try {

            brevoEmailService.sendEmail(
                    normalizedEmail,
                    "SHE SAFE - Super Admin Login OTP",
                    content
            );

        } catch (Exception e) {

            // Remove OTP if email sending fails
            otpStorage.remove(normalizedEmail);

            throw new RuntimeException(
                    "Unable to send Super Admin OTP email",
                    e
            );
        }
    }

    // VERIFY OTP

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
        if (LocalDateTime.now().isAfter(data.expiry)) {

            otpStorage.remove(normalizedEmail);
            return false;
        }

        // OTP does not match
        if (!data.otp.equals(otp)) {
            return false;
        }

        // OTP is correct - remove to prevent reuse
        otpStorage.remove(normalizedEmail);

        return true;
    }

    // OTP DATA

    private static class OtpData {

        private final String otp;
        private final LocalDateTime expiry;

        public OtpData(
                String otp,
                LocalDateTime expiry) {

            this.otp = otp;
            this.expiry = expiry;
        }
    }
}
