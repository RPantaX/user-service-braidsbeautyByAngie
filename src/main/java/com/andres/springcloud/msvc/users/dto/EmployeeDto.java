package com.andres.springcloud.msvc.users.dto;

import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeDto {
    private Long id;
    private String employeeImage;

    // Relaciones como IDs para requests
    private Long employeeTypeId;
    private Long userId;
    private Long personId;

    // Objetos anidados para respuestas completas
    private EmployeeTypeDto employeeType;
    private UserDto user;
    private PersonDto person;
    private List<ContractDto> contracts;

    // Campos calculados/derivados
    private String employeeName;
    private String employeeEmail;
}