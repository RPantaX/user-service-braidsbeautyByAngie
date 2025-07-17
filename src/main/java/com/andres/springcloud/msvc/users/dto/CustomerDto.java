package com.andres.springcloud.msvc.users.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDto {
    private Long id;
    private String phoneWhatsapp;

    // Relaciones como IDs
    private Long customerTypeId;
    private Long personId;

    // Objetos anidados para respuestas completas
    private CustomerTypeDto customerType;
    private PersonDto person;

    // Campos calculados/derivados
    private String customerName;
    private String customerEmail;
    private String primaryPhone;

    public String getCustomerName() {
        return person != null ? person.getFullName() : "";
    }

    public String getCustomerEmail() {
        return person != null ? person.getEmailAddress() : "";
    }

    public String getPrimaryPhone() {
        if (phoneWhatsapp != null && !phoneWhatsapp.isEmpty()) {
            return phoneWhatsapp;
        }
        return person != null ? person.getPhoneNumber() : "";
    }
}