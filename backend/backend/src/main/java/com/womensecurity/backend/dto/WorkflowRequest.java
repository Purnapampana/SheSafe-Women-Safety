package com.womensecurity.backend.dto;

public class WorkflowRequest {

    private Long userId;

    public WorkflowRequest() {
    }

    public WorkflowRequest(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}