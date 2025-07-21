package com.andres.springcloud.msvc.users.controllers;

import com.andres.springcloud.msvc.users.services.IUtilsService;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/user-service/utils")
@RequiredArgsConstructor
public class UtilsController {
    private final IUtilsService utilsService;
    @GetMapping("/list/employee-types")
    public ResponseEntity<ApiResponse> listAllEmployeeTypes(){
        return ResponseEntity.ok(ApiResponse.ok("List of employee types retrieved successfully",
                utilsService.getAllEmployeeTypes()));
    }
    @GetMapping("/list/document-types")
    public ResponseEntity<ApiResponse> listAllDocumentTypes(){
        return ResponseEntity.ok(ApiResponse.ok("List of document types retrieved successfully",
                utilsService.getAllDocumentTypes()));
    }
}
