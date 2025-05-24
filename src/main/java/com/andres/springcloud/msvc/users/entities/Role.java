package com.andres.springcloud.msvc.users.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="role")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;
    @Column(name = "role_name", unique = true)
    private String name;
}
