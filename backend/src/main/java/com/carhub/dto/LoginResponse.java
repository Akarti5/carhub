package com.carhub.dto;

import com.carhub.entity.Admin;

public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Admin admin;

    public LoginResponse(String token, Admin admin) {
        this.token = token;
        this.admin = admin;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Admin getAdmin() { return admin; }
    public void setAdmin(Admin admin) { this.admin = admin; }
}
