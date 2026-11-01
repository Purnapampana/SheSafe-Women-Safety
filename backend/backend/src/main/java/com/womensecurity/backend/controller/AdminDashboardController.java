package com.womensecurity.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.womensecurity.backend.dto.AdminDashboardResponse;
import com.womensecurity.backend.service.AdminDashboardService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService adminDashboardService;

    // ==========================================
    // ADMIN DASHBOARD
    // ==========================================

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> getDashboard() {

        AdminDashboardResponse response =
                adminDashboardService.getDashboard();

        return ResponseEntity.ok(response);
    }
}