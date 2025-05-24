package com.andres.springcloud.msvc.users.controllers;

import com.andres.springcloud.msvc.users.dto.UserRequest;
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
    public ResponseEntity<User> createUser(@RequestBody UserRequest user) {
        return new ResponseEntity<>(userService.save(user), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@RequestBody User user, @PathVariable Long id) {
        return userService.update(user, id).map(userUpdated -> ResponseEntity.status(HttpStatus.CREATED).body(userUpdated))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.findById(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return userService.findByUsername(username).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Iterable<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{userId}/validate")
    public ResponseEntity<Boolean> validateUser(@PathVariable String userId){
        return ResponseEntity.ok(userService.existByUserId(userId));
    }
}
