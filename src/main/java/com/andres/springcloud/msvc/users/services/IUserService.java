package com.andres.springcloud.msvc.users.services;

import java.util.Optional;

import com.andres.springcloud.msvc.users.dto.UserRequest;
import com.andres.springcloud.msvc.users.entities.User;

public interface IUserService {

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Iterable<User> findAll();

    User save(UserRequest user);
    Optional<User> update(User user, Long id);

    void delete(Long id);
    Boolean existByUserId(String userId);
}
