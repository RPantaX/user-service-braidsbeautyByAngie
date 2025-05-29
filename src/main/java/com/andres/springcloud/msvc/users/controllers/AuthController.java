package com.andres.springcloud.msvc.users.controllers;

import com.andres.springcloud.msvc.users.dto.AuthRequest;
import com.andres.springcloud.msvc.users.dto.TokenResponse;
import com.andres.springcloud.msvc.users.dto.TokenValidationResponse;
import com.andres.springcloud.msvc.users.services.AuthService;
import com.andres.springcloud.msvc.users.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/user-service/user/auth")
public class AuthController {

    @Autowired
    private AuthService service;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> getToken(@RequestBody AuthRequest authRequest) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        if (authenticate.isAuthenticated()) {
            String token = service.generateToken(authRequest.getUsername());
            TokenResponse tokenResponse = TokenResponse.builder().token(token).build();
            return ResponseEntity.ok(tokenResponse);
        } else {
            throw new RuntimeException("Invalid access");
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validateToken(@RequestParam("token") String token) {
        try {
            jwtService.validateToken(token);

            // Extraer información del token
            String username = jwtService.extractUsername(token);
            List<String> roles = jwtService.extractRoles(token);
            Long userId = jwtService.extractUserId(token);

            TokenValidationResponse response = new TokenValidationResponse();
            response.setValid(true);
            response.setUsername(username);
            response.setRoles(roles);
            response.setUserId(userId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            TokenValidationResponse response = new TokenValidationResponse();
            response.setValid(false);
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/validate-role")
    public ResponseEntity<Boolean> validateTokenAndRole(
            @RequestParam("token") String token,
            @RequestParam("role") String requiredRole) {
        try {
            jwtService.validateToken(token);
            List<String> roles = jwtService.extractRoles(token);
            boolean hasRole = roles.contains(requiredRole);
            return ResponseEntity.ok(hasRole);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
}