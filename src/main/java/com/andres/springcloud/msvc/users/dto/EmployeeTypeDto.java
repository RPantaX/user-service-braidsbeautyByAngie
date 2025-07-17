package com.andres.springcloud.msvc.users.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeTypeDto {
    private Long id;
    private String value;
}