package com.womensecurity.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "emergency_contacts")
public class EmergencyContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String contactName;

    @Column(nullable = false)
    private String contactNumber;

    @Column(nullable = false)
    private String relationship;

    @Column(nullable = false)
    private Integer priority;

    @Column(nullable = false)
    private String groupName;
    @Column(nullable = true)
private String contactEmail;

    public EmergencyContact() {
    }

    public EmergencyContact(
            Long id,
            Long userId,
            String contactName,
            String contactNumber,
            String relationship,
            Integer priority,
            String groupName,
            String contactEmail) {

        this.id = id;
        this.userId = userId;
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.relationship = relationship;
        this.priority = priority;
        this.groupName = groupName;
        this.contactEmail = contactEmail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

public String getContactEmail() {
    return contactEmail;
}

public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
}
}