package com.womensecurity.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.womensecurity.backend.dto.LoginRequest;
import com.womensecurity.backend.dto.LoginResponse;
import com.womensecurity.backend.dto.RegisterRequest;
import com.womensecurity.backend.dto.RegistrationOtpRequest;
import com.womensecurity.backend.dto.SuperAdminOtpRequest;
import com.womensecurity.backend.entity.User;
import com.womensecurity.backend.service.AuthenticationService;
import com.womensecurity.backend.service.SuperAdminOtpService;
import com.womensecurity.backend.service.UserService;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private SuperAdminOtpService superAdminOtpService;


    // =====================================================
    // REGISTER
    // =====================================================

    @PostMapping("/register")
    public String register(
            @RequestBody RegisterRequest request) {

        return userService.registerUser(request);
    }


    // =====================================================
    // VERIFY REGISTRATION OTP
    // =====================================================

    @PostMapping("/verify-registration-otp")
    public String verifyRegistrationOtp(
            @RequestBody RegistrationOtpRequest request) {

        return userService.verifyRegistrationOtp(request);
    }

@PostMapping("/resend-registration-otp")
public ResponseEntity<?> resendRegistrationOtp(
        @RequestBody Map<String, String> request) {

    String email =
            request.get("email");


    if (email == null ||
        email.trim().isEmpty()) {

        return ResponseEntity.badRequest()
                .body(
                        Map.of(
                                "message",
                                "EMAIL_REQUIRED"
                        )
                );
    }


    String result =
            userService.resendRegistrationOtp(email);


    if ("OTP resent successfully"
            .equals(result)) {

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "OTP_SENT"
                )
        );
    }


    return ResponseEntity.badRequest()
            .body(
                    Map.of(
                            "message",
                            result
                    )
            );
}




    // =====================================================
    // LOGIN
    // =====================================================

    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest request) {

        return authenticationService.login(request);
    }



    // =====================================================
    // VERIFY SUPER ADMIN OTP
    // =====================================================

    @PostMapping("/verify-super-admin-otp")
    public LoginResponse verifySuperAdminOtp(
            @RequestBody SuperAdminOtpRequest request) {

        return authenticationService.verifySuperAdminOtp(
                request.getEmail(),
                request.getOtp()
        );
    }


    // =====================================================
    // RESEND SUPER ADMIN OTP
    // =====================================================

    @PostMapping("/resend-super-admin-otp")
    public ResponseEntity<?> resendSuperAdminOtp(
            @RequestBody Map<String, String> request) {

        String email = request.get("email");


        // Check email
        if (email == null || email.trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "EMAIL_REQUIRED"
                            )
                    );
        }


        try {

            // Generate a NEW OTP
            // and send it to the email
            superAdminOtpService.resendOtp(email);


            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "OTP_SENT"
                    )
            );


        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError()
                    .body(
                            Map.of(
                                    "message",
                                    "OTP_SEND_FAILED"
                            )
                    );
        }
    }


    // =====================================================
    // READ ALL USERS
    // =====================================================

    @GetMapping("/users")
    public List<User> getAllUsers() {

        return userService.getAllUsers();
    }


    // =====================================================
    // READ USER BY ID
    // =====================================================

    @GetMapping("/user/{id}")
    public User getUser(
            @PathVariable Long id) {

        return userService.getUserById(id);
    }


    // =====================================================
    // UPDATE USER
    // =====================================================

    @PutMapping("/update/{id}")
    public User updateUser(
            @PathVariable Long id,
            @RequestBody User user) {

        return userService.updateUser(id, user);
    }


    // =====================================================
    // DELETE USER
    // =====================================================

    @DeleteMapping("/delete/{id}")
    public String deleteUser(
            @PathVariable Long id) {

        return userService.deleteUser(id);
    }
}

