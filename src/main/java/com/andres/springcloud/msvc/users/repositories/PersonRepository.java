package com.andres.springcloud.msvc.users.repositories;

import com.andres.springcloud.msvc.users.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    //findByEmail
    Optional<Person> findByEmailAddress(String emailAddress);
    //findByPhoneNumber
    Optional<Person> findByPhoneNumber(String phoneNumber);
    //findBy DocumentNumber
    Optional<Person> findByDocumentNumber(String documentNumber);
}
