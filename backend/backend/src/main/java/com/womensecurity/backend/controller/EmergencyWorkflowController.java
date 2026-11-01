package com.womensecurity.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.womensecurity.backend.dto.WorkflowRequest;
import com.womensecurity.backend.model.WorkflowResponse;
import com.womensecurity.backend.service.EmergencyWorkflowService;

@RestController
@RequestMapping("/api/workflow")
@CrossOrigin("*")
public class EmergencyWorkflowController {

    @Autowired
    private EmergencyWorkflowService workflowService;

    @PostMapping("/activate")
    public WorkflowResponse activateWorkflow(
            @RequestBody WorkflowRequest request) {

        return workflowService.activateWorkflow(request);

    }

}