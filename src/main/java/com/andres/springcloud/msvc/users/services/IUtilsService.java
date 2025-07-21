package com.andres.springcloud.msvc.users.services;

import com.andres.springcloud.msvc.users.dto.DocumentTypeDto;
import com.andres.springcloud.msvc.users.dto.EmployeeTypeDto;

import java.util.List;

public interface IUtilsService {
    List<EmployeeTypeDto> getAllEmployeeTypes();
    List<DocumentTypeDto> getAllDocumentTypes();
}
