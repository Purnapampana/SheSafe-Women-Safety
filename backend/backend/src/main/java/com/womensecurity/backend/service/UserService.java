package com.womensecurity.backend.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.womensecurity.backend.dto.LoginRequest;
import com.womensecurity.backend.dto.LoginResponse;
import com.womensecurity.backend.dto.RegisterRequest;
import com.womensecurity.backend.dto.RegistrationOtpRequest;
import com.womensecurity.backend.entity.User;
import com.womensecurity.backend.repository.UserRepository;
import com.womensecurity.backend.security.JwtUtil;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JavaMailSender mailSender;
    private final Map<String, PendingRegistration> pendingRegistrations =
        new ConcurrentHashMap<>();


    // =====================================================
    // REGISTER USER
    // =====================================================

public String registerUser(RegisterRequest request) {

    // ---------------------------------------------
    // VALIDATE REQUEST
    // ---------------------------------------------

    if (request.getEmail() == null ||
        request.getEmail().trim().isEmpty()) {

        return "Email is required!";
    }

    if (request.getPhoneNumber() == null ||
        request.getPhoneNumber().trim().isEmpty()) {

        return "Phone number is required!";
    }

    if (request.getPassword() == null ||
        request.getPassword().isEmpty()) {

        return "Password is required!";
    }
    String preferredLanguage = request.getPreferredLanguage();

if (preferredLanguage == null || preferredLanguage.isBlank()) {
    preferredLanguage = "en";
}

preferredLanguage = preferredLanguage.trim().toLowerCase(
        java.util.Locale.ROOT
);

if (!java.util.Set.of("en", "te", "hi", "ur")
        .contains(preferredLanguage)) {
    return "Unsupported preferred language!";
}


    // ---------------------------------------------
    // NORMALIZE DATA
    // ---------------------------------------------

    String email =
            request.getEmail()
                    .trim()
                    .toLowerCase();

    String phoneNumber =
            request.getPhoneNumber()
                    .trim();


    // ---------------------------------------------
    // CHECK EXISTING EMAIL
    // ---------------------------------------------

    if (userRepository.existsByEmail(email)) {

        return "Email already exists!";
    }


    // ---------------------------------------------
    // CHECK EXISTING PHONE
    // ---------------------------------------------

    if (userRepository.existsByPhoneNumber(phoneNumber)) {

        return "Phone number already exists!";
    }


    // ---------------------------------------------
    // GENERATE OTP
    // ---------------------------------------------

    String otp =
            generateRegistrationOtp();


    // ---------------------------------------------
    // STORE REGISTRATION DATA TEMPORARILY
    // ---------------------------------------------
    //
    // IMPORTANT:
    // NO USER IS SAVED TO DATABASE HERE.
    //

    PendingRegistration pending =
            new PendingRegistration(
                    request.getFullName().trim(),
                    email,
                    phoneNumber,
                    passwordEncoder.encode(
                            request.getPassword()
                    ),
                    otp,
                    LocalDateTime.now().plusMinutes(5),
                    preferredLanguage
            );


    pendingRegistrations.put(
            email,
            pending
    );


    // ---------------------------------------------
    // SEND OTP
    // ---------------------------------------------

    try {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(email);

        message.setSubject(
                "SheSafe - Registration OTP"
        );

        message.setText(

                "Hello "
                + request.getFullName().trim()
                + ",\n\n"

                + "Welcome to SheSafe.\n\n"

                + "Your registration OTP is:\n\n"

                + otp

                + "\n\nThis OTP is valid for 5 minutes."

                + "\n\nPlease do not share this OTP with anyone."

                + "\n\nIf you did not create a SheSafe account, "
                + "please ignore this email."

                + "\n\nSheSafe Security Team"
        );


        mailSender.send(message);


    } catch (Exception e) {

        e.printStackTrace();

        // Remove temporary registration
        pendingRegistrations.remove(email);

        return "Unable to send registration OTP. Please try again.";
    }


    return "OTP sent successfully";
}
private static class PendingRegistration {

    private String fullName;
    private String email;
    private String phoneNumber;
    private String password;
    private String otp;
    private LocalDateTime otpExpiry;
    private String preferredLanguage;


    public PendingRegistration(
            String fullName,
            String email,
            String phoneNumber,
            String password,
            String otp,
            LocalDateTime otpExpiry,
            String preferredLanguage) {

        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.otp = otp;
        this.otpExpiry = otpExpiry;
        this.preferredLanguage = preferredLanguage;
    }


    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public String getOtp() {
        return otp;
    }

    public LocalDateTime getOtpExpiry() {
        return otpExpiry;
    }
    public String getPreferredLanguage() {
    return preferredLanguage;
}
}
public String resendRegistrationOtp(String email) {

    // ---------------------------------------------
    // VALIDATE EMAIL
    // ---------------------------------------------

    if (email == null ||
        email.trim().isEmpty()) {

        return "Email is required!";
    }


    String normalizedEmail =
            email.trim().toLowerCase();


    // ---------------------------------------------
    // FIND TEMPORARY REGISTRATION
    // ---------------------------------------------

    PendingRegistration oldData =
            pendingRegistrations.get(normalizedEmail);


    if (oldData == null) {

        return "Registration not found. Please register again.";
    }


    // ---------------------------------------------
    // GENERATE NEW OTP
    // ---------------------------------------------

    String newOtp =
            generateRegistrationOtp();


    // ---------------------------------------------
    // CREATE NEW TEMPORARY DATA
    // ---------------------------------------------
    //
    // Keep the user's registration details,
    // but replace the old OTP and expiry.
    //

    PendingRegistration newData =
            new PendingRegistration(
                    oldData.getFullName(),
                    oldData.getEmail(),
                    oldData.getPhoneNumber(),
                    oldData.getPassword(),
                    newOtp,
                    LocalDateTime.now().plusMinutes(5),
                    oldData.getPreferredLanguage()
            );


    // Replace old OTP
    pendingRegistrations.put(
            normalizedEmail,
            newData
    );


    // ---------------------------------------------
    // SEND NEW OTP
    // ---------------------------------------------

    try {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(normalizedEmail);

        message.setSubject(
                "SheSafe - New Registration OTP"
        );

        message.setText(

                "Hello "
                + oldData.getFullName()
                + ",\n\n"

                + "Your new SheSafe registration OTP is:\n\n"

                + newOtp

                + "\n\nThis OTP is valid for 5 minutes."

                + "\n\nYour previous OTP is no longer valid."

                + "\n\nPlease do not share this OTP with anyone."

                + "\n\nSheSafe Security Team"
        );


        mailSender.send(message);


    } catch (Exception e) {

        e.printStackTrace();

        // If email fails, remove the newly
        // generated temporary registration.
        pendingRegistrations.remove(normalizedEmail);

        return "Unable to send OTP. Please register again.";
    }


    return "OTP resent successfully";
}






    // =====================================================
    // GENERATE REGISTRATION OTP
    // =====================================================

    private String generateRegistrationOtp() {

        SecureRandom random =
                new SecureRandom();


        int otp =
                100000 +
                random.nextInt(900000);


        return String.valueOf(otp);
    }


    // =====================================================
    // VERIFY REGISTRATION OTP
    // =====================================================


public String verifyRegistrationOtp(
        RegistrationOtpRequest request) {

    // ---------------------------------------------
    // VALIDATE EMAIL
    // ---------------------------------------------

    if (request.getEmail() == null ||
        request.getEmail().trim().isEmpty()) {

        return "Email is required!";
    }


    String email =
            request.getEmail()
                    .trim()
                    .toLowerCase();


    // ---------------------------------------------
    // VALIDATE OTP
    // ---------------------------------------------

    if (request.getOtp() == null ||
        request.getOtp().trim().isEmpty()) {

        return "Please enter the OTP.";
    }


    // ---------------------------------------------
    // FIND TEMPORARY REGISTRATION
    // ---------------------------------------------

    PendingRegistration pending =
            pendingRegistrations.get(email);


    if (pending == null) {

        return "Registration not found or OTP expired. Please register again.";
    }


    // ---------------------------------------------
    // CHECK OTP
    // ---------------------------------------------

    if (!pending.getOtp()
            .equals(request.getOtp().trim())) {

        return "Invalid OTP!";
    }


    // ---------------------------------------------
    // CHECK OTP EXPIRY
    // ---------------------------------------------

    if (LocalDateTime.now()
            .isAfter(pending.getOtpExpiry())) {

        pendingRegistrations.remove(email);

        return "OTP has expired!";
    }


    // =================================================
    // OTP VERIFIED
    // =================================================
    //
    // ONLY NOW CREATE THE USER
    //


    // ---------------------------------------------
    // DOUBLE CHECK EMAIL
    // ---------------------------------------------

    if (userRepository.existsByEmail(email)) {

        pendingRegistrations.remove(email);

        return "Email already exists!";
    }


    // ---------------------------------------------
    // DOUBLE CHECK PHONE
    // ---------------------------------------------

    if (userRepository
            .existsByPhoneNumber(
                    pending.getPhoneNumber()
            )) {

        pendingRegistrations.remove(email);

        return "Phone number already exists!";
    }


    // ---------------------------------------------
    // CREATE USER
    // ---------------------------------------------

    User user =
            new User();


    user.setFullName(
            pending.getFullName()
    );


    user.setEmail(
            pending.getEmail()
    );


    user.setPhoneNumber(
            pending.getPhoneNumber()
    );


    user.setPassword(
            pending.getPassword()
    );


    user.setRole("USER");


    // OTP has already been verified
    user.setAccountStatus("ACTIVE");

user.setPreferredLanguage(
        pending.getPreferredLanguage()
);
    // ---------------------------------------------
    // SAVE USER TO DATABASE
    // ---------------------------------------------

    userRepository.save(user);


    // ---------------------------------------------
    // REMOVE TEMPORARY REGISTRATION
    // ---------------------------------------------

    pendingRegistrations.remove(email);


    return "Registration verified successfully";
}




    // =====================================================
    // LOGIN
    // =====================================================

    public LoginResponse loginUser(
            LoginRequest request) {


        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();


        Optional<User> user =
                userRepository
                        .findByEmailIgnoreCase(email);


        if (user.isEmpty()) {

            throw new RuntimeException(
                    "User not found!"
            );
        }


        User existingUser =
                user.get();


        // =================================================
        // PASSWORD
        // =================================================

        if (!passwordEncoder.matches(
                request.getPassword(),
                existingUser.getPassword())) {

            throw new RuntimeException(
                    "Invalid Password!"
            );
        }


        // =================================================
        // ACCOUNT STATUS
        // =================================================

        if (!"ACTIVE".equalsIgnoreCase(
                existingUser.getAccountStatus())) {

            throw new RuntimeException(

                    "Your account is " +

                    existingUser
                            .getAccountStatus()
                            .toLowerCase() +

                    ". Please verify your registration or contact the administrator."
            );
        }


        // =================================================
        // GENERATE JWT
        // =================================================

        String token =
                jwtUtil.generateToken(
                        existingUser.getEmail()
                );


        // =================================================
        // RETURN LOGIN RESPONSE
        // =================================================

        return new LoginResponse(

                token,

                existingUser.getId(),

                existingUser.getRole(),

                existingUser.isProfileCompleted(),
                existingUser.getPreferredLanguage()
        );
    }


    // =====================================================
    // READ ALL USERS
    // =====================================================

    public List<User> getAllUsers() {

        return userRepository.findAll();
    }


    // =====================================================
    // READ USER BY ID
    // =====================================================

    public User getUserById(Long id) {

        return userRepository
                .findById(id)
                .orElse(null);
    }


    // =====================================================
    // UPDATE USER
    // =====================================================

    public User updateUser(
            Long id,
            User updatedUser) {


        User user =
                userRepository
                        .findById(id)
                        .orElse(null);


        if (user != null) {

            user.setFullName(
                    updatedUser.getFullName()
            );


            if (updatedUser.getEmail() != null) {

                user.setEmail(
                        updatedUser
                                .getEmail()
                                .trim()
                                .toLowerCase()
                );
            }


            if (updatedUser.getPhoneNumber() != null) {

                user.setPhoneNumber(
                        updatedUser
                                .getPhoneNumber()
                                .trim()
                );
            }


            if (updatedUser.getPassword() != null &&
                !updatedUser.getPassword().isBlank()) {

                user.setPassword(
                        passwordEncoder.encode(
                                updatedUser.getPassword()
                        )
                );
            }


            return userRepository.save(user);
        }


        return null;
    }


    // =====================================================
    // DELETE USER
    // =====================================================

    public String deleteUser(Long id) {

        if (userRepository.existsById(id)) {

            userRepository.deleteById(id);

            return "User Deleted Successfully";
        }


        return "User Not Found";
    }


    // =====================================================
    // BLOCK USER
    // =====================================================

    public User blockUser(Long id) {

        User user =
                userRepository
                        .findById(id)
                        .orElse(null);


        if (user == null) {
            return null;
        }


        user.setAccountStatus("BLOCKED");


        return userRepository.save(user);
    }


    // =====================================================
    // UNBLOCK USER
    // =====================================================

    public User unblockUser(Long id) {

        User user =
                userRepository
                        .findById(id)
                        .orElse(null);


        if (user == null) {
            return null;
        }


        user.setAccountStatus("ACTIVE");


        return userRepository.save(user);
    }


    // =====================================================
    // BAN USER
    // =====================================================

    public User banUser(Long id) {

        User user =
                userRepository
                        .findById(id)
                        .orElse(null);


        if (user == null) {
            return null;
        }


        user.setAccountStatus("BANNED");


        return userRepository.save(user);
    }
}