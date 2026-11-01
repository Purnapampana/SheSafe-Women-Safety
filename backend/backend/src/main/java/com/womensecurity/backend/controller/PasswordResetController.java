package com.womensecurity.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.womensecurity.backend.dto.ForgotPasswordRequest;
import com.womensecurity.backend.service.PasswordResetService;

@RestController
@RequestMapping("/api/password")
@CrossOrigin("*")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;


    // ================= SEND OTP =================

    @PostMapping("/send-otp")
    public String sendOtp(
            @RequestBody ForgotPasswordRequest request) {

        return passwordResetService.sendOtp(
                request.getEmail()
        );
    }


    // ================= RESET PASSWORD =================

    @PostMapping("/reset")
    public String resetPassword(
            @RequestBody ForgotPasswordRequest request) {

        return passwordResetService.resetPassword(

                request.getEmail(),

                request.getOtp(),

                request.getNewPassword()
        );
    }
}