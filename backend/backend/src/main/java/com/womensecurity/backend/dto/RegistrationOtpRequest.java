package com.womensecurity.backend.dto;

public class RegistrationOtpRequest {

    private String email;
    private String otp;

    public RegistrationOtpRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}