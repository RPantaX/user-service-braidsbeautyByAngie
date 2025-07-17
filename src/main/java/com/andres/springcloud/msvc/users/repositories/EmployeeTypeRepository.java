package com.andres.springcloud.msvc.users.repositories;

import com.andres.springcloud.msvc.users.entities.EmployeeType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeTypeRepository extends JpaRepository<EmployeeType, Long> {
}
