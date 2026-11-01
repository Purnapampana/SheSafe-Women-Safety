package com.womensecurity.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.womensecurity.backend.dto.ProfileRequest;
import com.womensecurity.backend.service.ProfileService;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin("*")
public class ProfileController {

    @Autowired
    private ProfileService profileService;


    // =====================================================
    // SAVE PROFILE
    // =====================================================

    @PostMapping("/save")
    public String saveProfile(
            @RequestBody ProfileRequest request) {

        return profileService.saveProfile(request);
    }


    // =====================================================
    // GET PROFILE
    // =====================================================

    @GetMapping("/user/{userId}")
    public ProfileRequest getProfile(
            @PathVariable Long userId) {

        return profileService.getProfile(userId);
    }
}