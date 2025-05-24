package com.andres.springcloud.msvc.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
}
