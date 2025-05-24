package com.andres.springcloud.msvc.users.services;

import com.andres.springcloud.msvc.users.dto.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.andres.springcloud.msvc.users.entities.Role;
import com.andres.springcloud.msvc.users.entities.User;
import com.andres.springcloud.msvc.users.repositories.RoleRepository;
import com.andres.springcloud.msvc.users.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Transactional(readOnly = true)
    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public User save(UserRequest userRequest) {
        //validate if user exists by username or email
        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            throw new RuntimeException("User already exists");
        }
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        User user = new User();
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setKeycloakId(userRequest.getKeycloakId());
        user.setEmail(userRequest.getEmail());
        user.setRoles(getRoles(userRequest));
        user.setEnabled(true);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public Optional<User> update(User user, Long id) {
        
        Optional<User> userOptional = this.findById(id);
        
        return userOptional.map(userDb -> {
            userDb.setEmail(user.getEmail());
            userDb.setUsername(user.getUsername());
            if (user.isEnabled() == null) {
                userDb.setEnabled(true);
            } else {
                userDb.setEnabled(user.isEnabled());
            }
            userDb.setRoles(getRolesForUpdated(user));
            
            return Optional.of(userRepository.save(userDb));
        }).orElseGet(() -> Optional.empty());

    }
    @Override
    public Boolean existByUserId(String keycloakId) {
        return userRepository.existsByKeycloakId(keycloakId);
    }
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private List<Role> getRoles(UserRequest user) {
        List<Role> roles = new ArrayList<>();
        Optional<Role> roleOptional = roleRepository.findByName("ROLE_USER");
        roleOptional.ifPresent(roles::add);

        if (user.isAdmin()) {
            Optional<Role> adminRoleOptional = roleRepository.findByName("ROLE_ADMIN");
            adminRoleOptional.ifPresent(roles::add);
        }
        return roles;
    }
    private List<Role> getRolesForUpdated(User user) {
        List<Role> roles = new ArrayList<>();
        Optional<Role> roleOptional = roleRepository.findByName("ROLE_USER");
        roleOptional.ifPresent(roles::add);

        if (user.isAdmin()) {
            Optional<Role> adminRoleOptional = roleRepository.findByName("ROLE_ADMIN");
            adminRoleOptional.ifPresent(roles::add);
        }
        return roles;
    }
}
