package com.andres.springcloud.msvc.users.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "address")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long id;

    @NotBlank
    @Column(name = "address_street")
    private String street;

    @NotBlank
    @Column(name = "address_city")
    private String city;

    @NotBlank
    @Column(name = "address_state")
    private String state;

    @NotBlank
    @Column(name = "address_postal_code")
    private String postalCode;
    @NotBlank
    @Column(name = "address_description")
    private String description;
    @NotBlank
    @Column(name = "address_country")
    private String country;
}
