package com.andres.springcloud.msvc.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentTypeDto {
    private Long id;

    @NotBlank
    private String value;
}