package com.womensecurity.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.womensecurity.backend.dto.ProfileRequest;
import com.womensecurity.backend.entity.User;
import com.womensecurity.backend.repository.UserRepository;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;


    // =====================================================
    // SAVE PROFILE
    // =====================================================

    public String saveProfile(ProfileRequest request) {

        User user =
                userRepository.findById(request.getUserId())
                        .orElseThrow(() ->
                                new RuntimeException("User not found"));


        user.setFullName(request.getFullName());

        user.setDateOfBirth(
                request.getDateOfBirth()
        );

        user.setBloodGroup(
                request.getBloodGroup()
        );

        user.setMedicalConditions(
                request.getMedicalConditions()
        );

        user.setMedications(
                request.getMedications()
        );

        user.setAllergies(
                request.getAllergies()
        );

        user.setEmergencyDetails(
                request.getEmergencyDetails()
        );

        user.setPreferredHospital(
                request.getPreferredHospital()
        );


        // Profile is completed

        user.setProfileCompleted(true);


        userRepository.save(user);


        return "Profile Saved Successfully";
    }


    // =====================================================
    // GET EXISTING PROFILE
    // =====================================================

    public ProfileRequest getProfile(Long userId) {

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException("User not found"));


        ProfileRequest response =
                new ProfileRequest();


        response.setUserId(user.getId());

        response.setFullName(
                user.getFullName()
        );

        response.setDateOfBirth(
                user.getDateOfBirth()
        );

        response.setBloodGroup(
                user.getBloodGroup()
        );

        response.setMedicalConditions(
                user.getMedicalConditions()
        );

        response.setMedications(
                user.getMedications()
        );

        response.setAllergies(
                user.getAllergies()
        );

        response.setEmergencyDetails(
                user.getEmergencyDetails()
        );

        response.setPreferredHospital(
                user.getPreferredHospital()
        );


        return response;
    }
}