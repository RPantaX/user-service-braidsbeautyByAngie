package com.andres.springcloud.msvc.users.dto;

import jakarta.validation.constraints.NotBlank;

public class UserRequest {
    private String keycloakId;
    @NotBlank
    private String username;

    @NotBlank
    private String password;
    @NotBlank
    private Boolean enabled;
    @NotBlank
    private String email;
    @NotBlank
    private boolean admin;

    public UserRequest() {
    }

    public UserRequest(String keycloakId, String username, String password, Boolean enabled, String email, boolean admin) {
        this.keycloakId = keycloakId;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.email = email;
        this.admin = admin;
    }

    public String getKeycloakId() {
        return keycloakId;
    }

    public void setKeycloakId(String keycloakId) {
        this.keycloakId = keycloakId;
    }

    public @NotBlank String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank String username) {
        this.username = username;
    }

    public @NotBlank String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank String password) {
        this.password = password;
    }

    public @NotBlank Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(@NotBlank Boolean enabled) {
        this.enabled = enabled;
    }

    public @NotBlank String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank String email) {
        this.email = email;
    }

    @NotBlank
    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(@NotBlank boolean admin) {
        this.admin = admin;
    }
}
