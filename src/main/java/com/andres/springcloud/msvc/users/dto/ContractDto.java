package com.andres.springcloud.msvc.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContractDto {
    private Long id;

    @NotNull
    @Positive
    private Integer timeMonth;

    @NotBlank
    private String kind;

    @NotBlank
    private String position;

    @NotNull
    @Positive
    private BigDecimal salary;

    @NotBlank
    private String state;

    @NotBlank
    private String employeeArea;

    private String employeeObs;

    // Relación como ID
    private Long employeeId;

    // Objeto anidado para respuestas
    private EmployeeDto employee;

    // Campos calculados
    private String salaryFormatted;
    private boolean isActive;

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(state) || "ACTIVO".equalsIgnoreCase(state);
    }

    public String getSalaryFormatted() {
        return salary != null ? "$" + salary.toString() : "$0.00";
    }
}