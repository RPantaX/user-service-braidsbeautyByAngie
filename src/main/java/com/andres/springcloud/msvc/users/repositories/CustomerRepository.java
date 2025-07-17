package com.andres.springcloud.msvc.users.repositories;

import com.andres.springcloud.msvc.users.repositories.dao.CustomerRepositoryCustom;
import com.andres.springcloud.msvc.users.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long>, CustomerRepositoryCustom {
    Optional<Customer> findByPersonId(Long personId);
}
