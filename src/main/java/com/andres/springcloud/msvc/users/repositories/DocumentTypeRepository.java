package com.andres.springcloud.msvc.users.repositories;

import com.andres.springcloud.msvc.users.entities.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {
}
