package com.andres.springcloud.msvc.users.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(name = "keycloak_id")
    private String keycloakId;
    @NotBlank
    @Column(unique = true)
    private String username;

    @NotBlank
    private String password;

    private Boolean enabled;
    
    @Transient
    private boolean admin;

    @JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
    @ManyToMany
    @JoinTable(name = "users_roles", 
            joinColumns = {@JoinColumn(name = "user_id") },
            inverseJoinColumns = {@JoinColumn(name = "role_id") }, 
            uniqueConstraints = {@UniqueConstraint(columnNames = { "user_id", "role_id" }) })
    private List<Role> roles;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    public Boolean isEnabled() {
        return enabled;
    }

    public boolean isAdmin() {
        return admin;
    }


}
