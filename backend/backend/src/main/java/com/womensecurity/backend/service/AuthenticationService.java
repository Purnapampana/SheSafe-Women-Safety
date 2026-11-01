package com.womensecurity.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.womensecurity.backend.dto.LoginRequest;
import com.womensecurity.backend.dto.LoginResponse;
import com.womensecurity.backend.entity.User;
import com.womensecurity.backend.repository.UserRepository;
import com.womensecurity.backend.security.JwtUtil;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
private SuperAdminOtpService superAdminOtpService;

    @Autowired
    private JwtUtil jwtUtil;


    // =====================================================
    // LOGIN
    // =====================================================

    public LoginResponse login(LoginRequest request) {

        LoginResponse response =
                new LoginResponse();


        // =================================================
        // NORMALIZE EMAIL
        // =================================================

        if (request.getEmail() == null ||
            request.getEmail().trim().isEmpty()) {

            response.setMessage(
                    "Email is required"
            );

            return response;
        }


        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();


        System.out.println(
                "LOGIN EMAIL: [" +
                email +
                "]"
        );


        // =================================================
        // FIND USER
        // =================================================

        User user =
                userRepository
                        .findByEmailIgnoreCase(email)
                        .orElse(null);


        // =================================================
        // USER NOT FOUND
        // =================================================

        if (user == null) {

            System.out.println(
                    "USER NOT FOUND: [" +
                    email +
                    "]"
            );

            response.setMessage(
                    "User not found"
            );

            return response;
        }


        System.out.println(
                "USER FOUND - ID: " +
                user.getId()
        );


        // =================================================
        // PASSWORD
        // =================================================

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            response.setMessage(
                    "Invalid Password"
            );

            return response;
        }


        // =================================================
        // ACCOUNT STATUS
        // =================================================

        if (!"ACTIVE".equalsIgnoreCase(
                user.getAccountStatus())) {

            response.setMessage(

                    "Your account is " +

                    user.getAccountStatus()
                            .toLowerCase() +

                    ". Please verify your registration OTP."
            );

            return response;
        }


        // =================================================
// SUPER ADMIN OTP
// =================================================

if ("SUPER_ADMIN".equalsIgnoreCase(
        user.getRole())) {

    superAdminOtpService.generateAndSendOtp(
            user.getEmail()
    );

    response.setMessage(
            "OTP_REQUIRED"
    );

    response.setUserId(
            user.getId()
    );

    response.setRole(
            user.getRole()
    );

    response.setProfileCompleted(
            user.isProfileCompleted()
    );

    return response;
}


// =================================================
// NORMAL USER JWT
// =================================================

String token =
        jwtUtil.generateToken(
                user.getEmail()
        );


        // =================================================
        // LOGIN SUCCESS
        // =================================================

        response.setMessage(
                "Login Successful"
        );


        response.setToken(
                token
        );


        response.setUserId(
                user.getId()
        );


        response.setRole(
                user.getRole()
        );


        response.setProfileCompleted(
                user.isProfileCompleted()
        );


        System.out.println(
                "LOGIN SUCCESS - USER ID: " +
                user.getId()
        );


        return response;
    }
    public LoginResponse verifySuperAdminOtp(
        String email,
        String otp) {

    LoginResponse response =
            new LoginResponse();

    if (email == null ||
        email.trim().isEmpty()) {

        response.setMessage(
                "Email is required"
        );

        return response;
    }

    if (otp == null ||
        otp.trim().isEmpty()) {

        response.setMessage(
                "OTP is required"
        );

        return response;
    }

    String normalizedEmail =
            email.trim().toLowerCase();

    User user =
            userRepository
                    .findByEmailIgnoreCase(
                            normalizedEmail
                    )
                    .orElse(null);

    if (user == null) {

        response.setMessage(
                "User not found"
        );

        return response;
    }

    if (!"SUPER_ADMIN".equalsIgnoreCase(
            user.getRole())) {

        response.setMessage(
                "Access denied"
        );

        return response;
    }

    boolean valid =
            superAdminOtpService.verifyOtp(
                    normalizedEmail,
                    otp.trim()
            );

    if (!valid) {

        response.setMessage(
                "Invalid or expired OTP"
        );

        return response;
    }

    String token =
            jwtUtil.generateToken(
                    user.getEmail()
            );

    response.setMessage(
            "OTP_VERIFIED"
    );

    response.setToken(token);

    response.setUserId(
            user.getId()
    );

    response.setRole(
            user.getRole()
    );

    response.setProfileCompleted(
            user.isProfileCompleted()
    );

    return response;
}
}