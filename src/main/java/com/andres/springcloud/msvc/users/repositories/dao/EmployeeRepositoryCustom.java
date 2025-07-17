package com.andres.springcloud.msvc.users.repositories.dao;

import com.andres.springcloud.msvc.users.entities.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeRepositoryCustom {
    Page<Employee> findAllActiveEmployeesWithRelations(Pageable pageable);
}