package com.andres.springcloud.msvc.users.repositories.dao;

import com.andres.springcloud.msvc.users.entities.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerRepositoryCustom {
    Page<Customer> findAllActiveCustomersWithRelations(Pageable pageable);
}
