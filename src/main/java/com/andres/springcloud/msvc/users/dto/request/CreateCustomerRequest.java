package com.andres.springcloud.msvc.users.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCustomerRequest {
    @NotNull
    private Long customerTypeId;

    @NotBlank
    private String name;

    @NotBlank
    private String lastName;

    private String phoneNumber;
    @NotBlank
    private String phoneWhatsapp;

    @Email
    private String emailAddress;

    // Address data
    @NotBlank
    private String street;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String postalCode;

    @NotBlank
    private String country;
    @NotBlank
    private String documentNumber;

    private Long documentTypeId;
    @NotBlank
    private String addressDescription;
}
