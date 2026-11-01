package com.womensecurity.backend.dto;

public class LoginResponse {

    private String message;
    private String token;
    private Long userId;
    private String role;
    private boolean profileCompleted;
     private String preferredLanguage;

    public LoginResponse() {
    }

    public LoginResponse(
            String token,
            Long userId,
            String role,
            boolean profileCompleted,
            String preferredLanguage) {

        this.token = token;
        this.userId = userId;
        this.role = role;
        this.profileCompleted = profileCompleted;
        this.preferredLanguage = preferredLanguage;
    }

    // ================= MESSAGE =================

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // ================= TOKEN =================

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // ================= USER ID =================

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // ================= ROLE =================

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // ================= PROFILE COMPLETED =================

    public boolean isProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

    // ================= PREFERRED LANGUAGE =================

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }
}