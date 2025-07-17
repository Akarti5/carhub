package com.carhub.controller;

import com.carhub.entity.Admin;
import com.carhub.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/public/debug")
@CrossOrigin(origins = "*")
public class DebugController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/check-admin")
    public ResponseEntity<Map<String, Object>> checkAdmin() {
        Map<String, Object> response = new HashMap<>();
        
        Optional<Admin> adminOpt = adminRepository.findByUsername("admin");
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            response.put("adminExists", true);
            response.put("username", admin.getUsername());
            response.put("email", admin.getEmail());
            response.put("role", admin.getRole());
            response.put("active", admin.isActive());
            response.put("passwordSet", admin.getPassword() != null && !admin.getPassword().isEmpty());
        } else {
            response.put("adminExists", false);
            response.put("message", "Admin user not found");
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/test-password")
    public ResponseEntity<Map<String, Object>> testPassword(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String username = request.get("username");
        String password = request.get("password");
        
        Optional<Admin> adminOpt = adminRepository.findByUsername(username);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            boolean passwordMatches = passwordEncoder.matches(password, admin.getPassword());
            response.put("passwordMatches", passwordMatches);
            response.put("username", username);
            response.put("hashedPassword", admin.getPassword().substring(0, 20) + "...");
        } else {
            response.put("error", "User not found");
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-admin")
    public ResponseEntity<Map<String, Object>> createAdmin() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Delete existing admin if exists
            adminRepository.findByUsername("admin").ifPresent(adminRepository::delete);
            
            // Create new admin
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setEmail("admin@carhub.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setRole(Admin.Role.SUPER_ADMIN);
            admin.setActive(true);
            
            adminRepository.save(admin);
            
            response.put("success", true);
            response.put("message", "Admin user created successfully");
            response.put("username", "admin");
            response.put("password", "admin123");
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}

