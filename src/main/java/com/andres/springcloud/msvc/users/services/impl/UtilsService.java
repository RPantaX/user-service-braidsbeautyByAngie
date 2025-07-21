package com.andres.springcloud.msvc.users.services.impl;

import com.andres.springcloud.msvc.users.dto.DocumentTypeDto;
import com.andres.springcloud.msvc.users.dto.EmployeeTypeDto;
import com.andres.springcloud.msvc.users.repositories.DocumentTypeRepository;
import com.andres.springcloud.msvc.users.repositories.EmployeeTypeRepository;
import com.andres.springcloud.msvc.users.services.IUtilsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class UtilsService implements IUtilsService {

    private final EmployeeTypeRepository employeeTypeRepository;
    private final DocumentTypeRepository documentTypeRepository;

    @Override
    public List<EmployeeTypeDto> getAllEmployeeTypes() {
        return employeeTypeRepository.findAll()
                .stream()
                .map(employeeType -> EmployeeTypeDto.builder()
                        .id(employeeType.getId())
                        .value(employeeType.getValue())
                        .build())
                .toList();
    }

    @Override
    public List<DocumentTypeDto> getAllDocumentTypes() {
        return documentTypeRepository.findAll()
                .stream()
                .map(documentType -> DocumentTypeDto.builder()
                        .id(documentType.getId())
                        .value(documentType.getValue())
                        .build())
                .toList();
    }
}
