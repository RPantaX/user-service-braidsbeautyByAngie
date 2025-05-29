package com.andres.springcloud.msvc.users.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenResponse {
    private String token;
}
