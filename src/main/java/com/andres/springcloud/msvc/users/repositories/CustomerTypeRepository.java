package com.andres.springcloud.msvc.users.repositories;

import com.andres.springcloud.msvc.users.entities.CustomerType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerTypeRepository extends JpaRepository<CustomerType, Long> {
}
