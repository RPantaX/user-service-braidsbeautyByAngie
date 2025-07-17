package com.andres.springcloud.msvc.users.repositories;

import com.andres.springcloud.msvc.users.entities.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Long> {
}
