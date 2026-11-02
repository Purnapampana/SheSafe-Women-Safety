
package com.womensecurity.backend.service;

import java.time.LocalDateTime;
import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.womensecurity.backend.entity.User;
import com.womensecurity.backend.repository.UserRepository;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BrevoEmailService brevoEmailService;

    // ================= SEND OTP =================

    public String sendOtp(String email) {

        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            return "Email not registered";
        }

        // Generate 6-digit OTP
        String otp = String.format(
                "%06d",
                new SecureRandom().nextInt(1000000)
        );

        // OTP valid for 5 minutes
        user.setResetOtp(otp);

        user.setResetOtpExpiry(
                LocalDateTime.now().plusMinutes(5)
        );

        userRepository.save(user);

        // Send OTP using Brevo
        String content =
                "Hello " + user.getFullName() + ",\n\n"
                + "Your SheSafe password reset OTP is:\n\n"
                + otp
                + "\n\nThis OTP is valid for 5 minutes."
                + "\n\nIf you did not request this, please ignore this email."
                + "\n\nSheSafe Security Team";

        brevoEmailService.sendEmail(
                email,
                user.getFullName(),
                "SheSafe - Password Reset OTP",
                content
        );

        return "OTP sent successfully";
    }

    // ================= RESET PASSWORD =================

    public String resetPassword(
            String email,
            String otp,
            String newPassword) {

        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            return "Email not registered";
        }

        // Check OTP
        if (user.getResetOtp() == null ||
                !user.getResetOtp().equals(otp)) {
            return "Invalid OTP";
        }

        // Check OTP expiry
        if (user.getResetOtpExpiry() == null ||
                LocalDateTime.now().isAfter(
                        user.getResetOtpExpiry())) {
            return "OTP expired";
        }

        // Encrypt new password
        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        // Clear OTP
        user.setResetOtp(null);
        user.setResetOtpExpiry(null);

        userRepository.save(user);

        return "Password reset successfully";
    }
}
