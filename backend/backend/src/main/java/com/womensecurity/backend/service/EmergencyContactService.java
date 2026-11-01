package com.womensecurity.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.womensecurity.backend.dto.EmergencyContactRequest;
import com.womensecurity.backend.entity.EmergencyContact;
import com.womensecurity.backend.repository.EmergencyContactRepository;

@Service
public class EmergencyContactService {

    @Autowired
    private EmergencyContactRepository emergencyContactRepository;


    // ================= ADD CONTACT =================

    public String addContact(EmergencyContactRequest request) {

        EmergencyContact contact = new EmergencyContact();

        contact.setUserId(request.getUserId());
        contact.setContactName(request.getContactName());
        contact.setContactNumber(request.getContactNumber());
        contact.setContactEmail(request.getContactEmail());
        contact.setRelationship(request.getRelationship());
        contact.setPriority(request.getPriority());
        contact.setGroupName(request.getGroupName());

        emergencyContactRepository.save(contact);

        return "Emergency Contact Added Successfully";
    }


    // ================= GET USER CONTACTS =================

    public List<EmergencyContact> getContacts(Long userId) {

        return emergencyContactRepository.findByUserId(userId);
    }


    // ================= GET ALL CONTACTS =================

    public List<EmergencyContact> getAllContacts() {

        return emergencyContactRepository.findAll();
    }


    // ================= UPDATE CONTACT =================

    public EmergencyContact updateContact(
            Long id,
            EmergencyContact updatedContact) {

        Optional<EmergencyContact> optionalContact =
                emergencyContactRepository.findById(id);

        if (optionalContact.isEmpty()) {
            return null;
        }

        EmergencyContact existing =
                optionalContact.get();

        existing.setContactName(
                updatedContact.getContactName());

        existing.setContactNumber(
                updatedContact.getContactNumber());
                existing.setContactEmail(
        updatedContact.getContactEmail());

        existing.setRelationship(
                updatedContact.getRelationship());

        existing.setPriority(
                updatedContact.getPriority());

        existing.setGroupName(
                updatedContact.getGroupName());

        return emergencyContactRepository.save(existing);
    }


    // ================= DELETE CONTACT =================

    public String deleteContact(Long id) {

        if (!emergencyContactRepository.existsById(id)) {

            return "Contact Not Found";
        }

        emergencyContactRepository.deleteById(id);

        return "Emergency Contact Deleted Successfully";
    }
}