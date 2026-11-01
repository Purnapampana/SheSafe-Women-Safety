package com.womensecurity.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;
    @Column(nullable = false)
private String accountStatus = "ACTIVE";

private String resetOtp;

private java.time.LocalDateTime resetOtpExpiry;
private String registrationOtp;

private java.time.LocalDateTime registrationOtpExpiry;
    @Column(nullable = false)
private boolean profileCompleted = false;

private String dateOfBirth;

private String bloodGroup;

@Column(length = 1000)
private String medicalConditions;

@Column(length = 1000)
private String medications;

@Column(length = 1000)
private String allergies;

@Column(length = 1000)
private String emergencyDetails;

private String preferredHospital;
@Column(name = "preferred_language", nullable = false, length = 10)
private String preferredLanguage = "en";


    public User() {
    }

    public User(Long id,
                String fullName,
                String email,
                String phoneNumber,
                String password,
                String role) {

        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    public boolean isProfileCompleted() {
    return profileCompleted;
}

public void setProfileCompleted(boolean profileCompleted) {
    this.profileCompleted = profileCompleted;
}

public String getDateOfBirth() {
    return dateOfBirth;
}

public void setDateOfBirth(String dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
}

public String getBloodGroup() {
    return bloodGroup;
}

public void setBloodGroup(String bloodGroup) {
    this.bloodGroup = bloodGroup;
}

public String getMedicalConditions() {
    return medicalConditions;
}

public void setMedicalConditions(String medicalConditions) {
    this.medicalConditions = medicalConditions;
}

public String getMedications() {
    return medications;
}

public void setMedications(String medications) {
    this.medications = medications;
}

public String getAllergies() {
    return allergies;
}

public void setAllergies(String allergies) {
    this.allergies = allergies;
}

public String getEmergencyDetails() {
    return emergencyDetails;
}

public void setEmergencyDetails(String emergencyDetails) {
    this.emergencyDetails = emergencyDetails;
}

public String getPreferredHospital() {
    return preferredHospital;
}

public void setPreferredHospital(String preferredHospital) {
    this.preferredHospital = preferredHospital;
}
public String getResetOtp() {
    return resetOtp;
}

public void setResetOtp(String resetOtp) {
    this.resetOtp = resetOtp;
}

public java.time.LocalDateTime getResetOtpExpiry() {
    return resetOtpExpiry;
}

public void setResetOtpExpiry(
        java.time.LocalDateTime resetOtpExpiry) {

    this.resetOtpExpiry = resetOtpExpiry;
}
public String getAccountStatus() {
    return accountStatus;
}

public void setAccountStatus(String accountStatus) {
    this.accountStatus = accountStatus;
}
public String getRegistrationOtp() {
    return registrationOtp;
}

public void setRegistrationOtp(String registrationOtp) {
    this.registrationOtp = registrationOtp;
}

public java.time.LocalDateTime getRegistrationOtpExpiry() {
    return registrationOtpExpiry;
}

public void setRegistrationOtpExpiry(
        java.time.LocalDateTime registrationOtpExpiry) {

    this.registrationOtpExpiry = registrationOtpExpiry;
}
public String getPreferredLanguage() {
    return preferredLanguage;
}

public void setPreferredLanguage(String preferredLanguage) {
    this.preferredLanguage = preferredLanguage;
}

}