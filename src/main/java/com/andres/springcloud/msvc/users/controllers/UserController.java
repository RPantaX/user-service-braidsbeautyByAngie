package com.andres.springcloud.msvc.users.controllers;

import com.andres.springcloud.msvc.users.dto.UserRequest;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.andres.springcloud.msvc.users.entities.User;
import com.andres.springcloud.msvc.users.services.IUserService;

@RestController
@RequestMapping("/v1/user-service/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse> createUser(@RequestBody UserRequest user) {
        return new ResponseEntity<>(ApiResponse.create("user created",userService.save(user)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateUser(@RequestBody User user, @PathVariable Long id) {
        return new ResponseEntity<>(ApiResponse.create("user updated", userService.update(user, id)), HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("User retrieved successfully",
                userService.findById(id)));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(ApiResponse.ok("User retrieved successfully",
                userService.findByUsername(username)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.ok("List of users retrieved successfully",
                userService.findAll()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("User deleted successfully", null));
    }
    @GetMapping("/{userId}/validate")
    public ResponseEntity<ApiResponse> validateUser(@PathVariable String userId){
        return ResponseEntity.ok(ApiResponse.ok("User validation result",
                userService.existByUserId(userId)));
    }
    @GetMapping("/roles")
    public ResponseEntity<ApiResponse> getAllRoles() {
        return ResponseEntity.ok(ApiResponse.ok("List of roles retrieved successfully",
                userService.getAllRoles()));
    }
}
