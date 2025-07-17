package com.carhub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "Carhub Backend API");
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "Carhub - Car Dealership Management System");
        response.put("description", "Backend API for car dealership management");
        response.put("version", "1.0.0");
        response.put("endpoints", new String[]{
            "POST /api/auth/login - Login",
            "GET /api/auth/profile - Get user profile (requires auth)",
            "GET /api/cars - Get cars (requires auth)",
            "GET /api/dashboard/stats - Get dashboard stats (requires auth)",
            "GET /api/public/health - Health check",
            "GET /api/public/info - API information"
        });
        return ResponseEntity.ok(response);
    }
}
