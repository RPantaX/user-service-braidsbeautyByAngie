package com.andres.springcloud.msvc.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonDto {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String lastName;

    private String phoneNumber;

    @Email
    private String emailAddress;

    // Relaciones como IDs
    private Long addressId;
    private Long documentTypeId;

    // Objetos anidados para respuestas completas
    private AddressDto address;
    private DocumentTypeDto documentType;

    // Campo calculado para nombre completo
    public String getFullName() {
        return name + " " + lastName;
    }
}