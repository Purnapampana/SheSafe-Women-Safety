package com.womensecurity.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.womensecurity.backend.dto.EmergencyContactRequest;
import com.womensecurity.backend.entity.EmergencyContact;
import com.womensecurity.backend.service.EmergencyContactService;

@RestController
@RequestMapping("/api/emergency")
@CrossOrigin("*")
public class EmergencyContactController {

    @Autowired
    private EmergencyContactService emergencyContactService;


    // ================= ADD =================

    @PostMapping("/add")
    public String addContact(
            @RequestBody EmergencyContactRequest request) {

        return emergencyContactService.addContact(request);
    }


    // ================= GET USER CONTACTS =================

    @GetMapping("/user/{userId}")
    public List<EmergencyContact> getContacts(
            @PathVariable Long userId) {

        return emergencyContactService.getContacts(userId);
    }


    // ================= GET ALL =================

    @GetMapping("/all")
    public List<EmergencyContact> getAllContacts() {

        return emergencyContactService.getAllContacts();
    }


    // ================= UPDATE =================

    @PutMapping("/update/{id}")
    public EmergencyContact updateContact(
            @PathVariable Long id,
            @RequestBody EmergencyContact contact) {

        return emergencyContactService.updateContact(
                id,
                contact);
    }


    // ================= DELETE =================

    @DeleteMapping("/delete/{id}")
    public String deleteContact(
            @PathVariable Long id) {

        return emergencyContactService.deleteContact(id);
    }
}