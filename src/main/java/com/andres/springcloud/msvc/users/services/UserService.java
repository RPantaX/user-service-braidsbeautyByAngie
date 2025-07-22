package com.andres.springcloud.msvc.users.services;

import com.andres.springcloud.msvc.users.dto.UserRequest;
import com.andres.springcloud.msvc.users.dto.constants.Constants;
import com.andres.springcloud.msvc.users.dto.constants.UsersErrorEnum;
import com.andres.springcloud.msvc.users.entities.Employee;
import com.andres.springcloud.msvc.users.entities.Person;
import com.andres.springcloud.msvc.users.repositories.EmployeeRepository;
import com.andres.springcloud.msvc.users.repositories.PersonRepository;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.util.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class UserService implements IUserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElse(null);
        if (user == null) {
            log.error("User with username {} not found", username);
            ValidateUtil.requerido(null, UsersErrorEnum.USER_NOT_FOUND_US00001);
        }
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
            ValidateUtil.evaluar(false, UsersErrorEnum.USERNAME_ALREADY_EXISTS_US00004);
        }
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            ValidateUtil.evaluar(false, UsersErrorEnum.USER_EMAIL_ALREADY_EXISTS_US00005);
        }
        User user = new User();
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setKeycloakId(userRequest.getKeycloakId());
        user.setEmail(userRequest.getEmail());
        user.setRoles(getRoles(userRequest));
        user.setUsername(userRequest.getUsername());
        user.setEnabled(true);
        user.setState(Constants.STATUS_ACTIVE);
        user.setCreatedAt(Constants.getTimestamp());
        user.setModifiedByUser(Constants.getUserInSession().isEmpty() ? "SYSTEM" : Constants.getUserInSession());
        User userSaved = userRepository.save(user);
        if(userRequest.getDocument() != null ){
            Person person = personRepository.findByDocumentNumber(userRequest.getDocument()).orElse(null);
            if (person ==null) {
                ValidateUtil.requerido(null, UsersErrorEnum.PERSON_NOT_FOUND_ERPE00001);
            }
            Employee employee = employeeRepository.findByPersonId(person.getId()).orElse(null);
            if(employee == null){
                ValidateUtil.requerido(null, UsersErrorEnum.EMPLOYEE_NOT_FOUND_ERE00005);
            }
            employee.setUser(userSaved);
            employeeRepository.save(employee);
        }
        return userSaved;
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

    @Override
    public List<Role> getAllRoles() {
        return (List<Role>) roleRepository.findAll();
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
