package com.carhub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class RootController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to Carhub API");
        response.put("status", "Server is running");
        response.put("documentation", "Visit /api/public/info for API endpoints");
        response.put("health", "Visit /api/public/health for health check");
        return ResponseEntity.ok(response);
    }
}
