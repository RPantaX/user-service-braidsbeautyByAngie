package com.andres.springcloud.msvc.users.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "employee_type")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_type_id")
    private Long id;

    @NotBlank
    @Column(name = "employee_type_value", unique = true)
    private String value;
}