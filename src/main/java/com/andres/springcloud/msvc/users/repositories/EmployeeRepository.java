package com.andres.springcloud.msvc.users.repositories;

import com.andres.springcloud.msvc.users.repositories.dao.EmployeeRepositoryCustom;
import com.andres.springcloud.msvc.users.entities.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, EmployeeRepositoryCustom {
    //findByPersonId
    Optional<Employee> findByPersonId(Long personId);
    @Query(value = "SELECT e FROM Employee e WHERE e.state = true")
    Page<Employee> findAllByStateTrueAndPageable(Pageable pageable);
}
