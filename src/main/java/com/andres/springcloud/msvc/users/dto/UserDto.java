package com.andres.springcloud.msvc.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String keycloakId;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private Boolean enabled;
    private boolean admin;

    @Email
    @NotBlank
    private String email;

    private List<Long> roleIds;
    private List<RoleDto> roles;
}