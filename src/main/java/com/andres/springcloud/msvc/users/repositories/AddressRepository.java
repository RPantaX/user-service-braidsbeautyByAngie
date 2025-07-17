package com.andres.springcloud.msvc.users.repositories;

import com.andres.springcloud.msvc.users.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
