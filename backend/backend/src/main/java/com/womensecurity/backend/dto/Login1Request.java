package com.womensecurity.backend.dto;

public class Login1Request {

    private String email;

    private String password;

    public Login1Request() {
    }

    public Login1Request(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}