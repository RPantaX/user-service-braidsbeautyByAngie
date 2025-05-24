package com.andres.springcloud.msvc.users.dto;

import java.util.List;

public class TokenValidationResponse {
    private boolean valid;
    private String username;
    private List<String> roles;
    private Long userId;

    public TokenValidationResponse() {}

    public TokenValidationResponse(boolean valid, String username, List<String> roles, Long userId) {
        this.valid = valid;
        this.username = username;
        this.roles = roles;
        this.userId = userId;
    }

    // Getters and Setters
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}