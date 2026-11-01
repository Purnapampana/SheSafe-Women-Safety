package com.womensecurity.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.womensecurity.backend.entity.IncidentTimeline;
import com.womensecurity.backend.repository.IncidentTimelineRepository;

@RestController
@RequestMapping("/api/admin/incidents")
@CrossOrigin("*")
public class AdminIncidentController {

    @Autowired
    private IncidentTimelineRepository incidentTimelineRepository;

    @GetMapping
    public List<IncidentTimeline> getAllIncidents() {

        return incidentTimelineRepository.findAll();
    }
}