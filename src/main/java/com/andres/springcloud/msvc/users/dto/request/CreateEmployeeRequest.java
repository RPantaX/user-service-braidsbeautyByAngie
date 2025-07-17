package com.andres.springcloud.msvc.users.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateEmployeeRequest {
    @NotNull
    private Long employeeTypeId;

    @NotBlank
    private String name;

    @NotBlank
    private String lastName;

    @NotBlank
    private String phoneNumber;

    @Email
    private String emailAddress;

    private MultipartFile employeeImage;

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
    private String addressDescription;
    @NotBlank
    private String country;
    @NotBlank
    private String documentNumber;
    private Long documentTypeId;
    private boolean deleteFile;
}
