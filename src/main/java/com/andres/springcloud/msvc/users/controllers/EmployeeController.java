package com.andres.springcloud.msvc.users.controllers;

import com.andres.springcloud.msvc.users.dto.enums.EmployeeTypeEnum;
import com.andres.springcloud.msvc.users.dto.request.CreateEmployeeRequest;
import com.andres.springcloud.msvc.users.services.IEmployeeService;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.Constants;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/user-service/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final IEmployeeService employeeService;

    @GetMapping("/list/pageable")
    public ResponseEntity<ApiResponse> listEmployeePageableList(@RequestParam(value = "pageNo", defaultValue = Constants.NUM_PAG_BY_DEFECT, required = false) int pageNo,
                                                                @RequestParam(value = "pageSize", defaultValue = Constants.SIZE_PAG_BY_DEFECT, required = false) int pageSize,
                                                                @RequestParam(value = "sortBy", defaultValue = Constants.ORDER_BY_DEFECT_ALL, required = false) String sortBy,
                                                                @RequestParam(value = "sortDir", defaultValue = Constants.ORDER_DIRECT_BY_DEFECT, required = false) String sortDir,
                                                                @RequestParam(value = "state", defaultValue = "true", required = false) boolean state) {
        return ResponseEntity.ok(ApiResponse.ok("List of employees retrieved successfully",
                employeeService.listEmployeePageable(pageNo, pageSize, sortBy, sortDir, state)));
    }
    @GetMapping("/list/pageable/by-typeId")
    public ResponseEntity<ApiResponse> listEmployeePageableByType(
            @RequestParam(value = "pageNo", defaultValue = Constants.NUM_PAG_BY_DEFECT, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = Constants.SIZE_PAG_BY_DEFECT, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = Constants.ORDER_BY_DEFECT_ALL, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = Constants.ORDER_DIRECT_BY_DEFECT, required = false) String sortDir,
            @RequestParam(value = "state", defaultValue = "true", required = false) boolean state,
            @RequestParam(value = "employeeTypeId", required = true) Long employeeTypeId) {
        return ResponseEntity.ok(ApiResponse.ok("List of employees by type retrieved successfully",
                employeeService.listEmployeePageableByType(pageNo, pageSize, sortBy, sortDir, employeeTypeId, state)));
    }

    // NUEVO: Endpoint para filtrar por enum de tipo de empleado
    @GetMapping("/list/pageable/by-type-enum")
    public ResponseEntity<ApiResponse> listEmployeePageableByTypeEnum(
            @RequestParam(value = "pageNo", defaultValue = Constants.NUM_PAG_BY_DEFECT, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = Constants.SIZE_PAG_BY_DEFECT, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = Constants.ORDER_BY_DEFECT_ALL, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = Constants.ORDER_DIRECT_BY_DEFECT, required = false) String sortDir,
            @RequestParam(value = "employeeType", required = true) EmployeeTypeEnum employeeType) {
        return ResponseEntity.ok(ApiResponse.ok("List of employees by type retrieved successfully",
                employeeService.listEmployeePageableByTypeEnum(pageNo, pageSize, sortBy, sortDir, employeeType)));
    }

    // NUEVO: Endpoint para filtrar por múltiples tipos
    @GetMapping("/list/pageable/by-multiple-typesIds")
    public ResponseEntity<ApiResponse> listEmployeePageableByMultipleTypes(
            @RequestParam(value = "pageNo", defaultValue = Constants.NUM_PAG_BY_DEFECT, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = Constants.SIZE_PAG_BY_DEFECT, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = Constants.ORDER_BY_DEFECT_ALL, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = Constants.ORDER_DIRECT_BY_DEFECT, required = false) String sortDir,
            @RequestParam(value = "employeeTypeIds", required = true) Long[] employeeTypeIds) {
        return ResponseEntity.ok(ApiResponse.ok("List of employees by multiple types retrieved successfully",
                employeeService.listEmployeePageableByMultipleTypes(pageNo, pageSize, sortBy, sortDir, employeeTypeIds)));
    }
    // NUEVO: Endpoint para obtener empleados por lista de IDs (sin paginación)
    @PostMapping("/list/by-ids")
    public ResponseEntity<ApiResponse> getEmployeesByIds(@RequestBody List<Long> employeeIds) {
        return ResponseEntity.ok(ApiResponse.ok("Employees retrieved successfully by IDs",
                employeeService.getEmployeesByIds(employeeIds)));
    }

    // NUEVO: Endpoint para obtener empleados activos por lista de IDs (sin paginación)
    @PostMapping("/list/active-by-ids")
    public ResponseEntity<ApiResponse> getActiveEmployeesByIds(@RequestBody List<Long> employeeIds) {
        return ResponseEntity.ok(ApiResponse.ok("Active employees retrieved successfully by IDs",
                employeeService.getActiveEmployeesByIds(employeeIds)));
    }

    // NUEVO: Endpoint alternativo con query params para IDs pequeñas
    @GetMapping("/list/by-ids")
    public ResponseEntity<ApiResponse> getEmployeesByIdsQueryParam(
            @RequestParam(value = "ids", required = true) List<Long> employeeIds) {
        return ResponseEntity.ok(ApiResponse.ok("Employees retrieved successfully by IDs",
                employeeService.getEmployeesByIds(employeeIds)));
    }

    // NUEVO: Endpoint alternativo con query params para empleados activos
    @GetMapping("/list/active-by-ids")
    public ResponseEntity<ApiResponse> getActiveEmployeesByIdsQueryParam(
            @RequestParam(value = "ids", required = true) List<Long> employeeIds) {
        return ResponseEntity.ok(ApiResponse.ok("Active employees retrieved successfully by IDs",
                employeeService.getActiveEmployeesByIds(employeeIds)));
    }
    @GetMapping("/findById/{employeeId}")
    public ResponseEntity<ApiResponse> findEmployeeById(@PathVariable(name = "employeeId") Long employeeId) {
        return ResponseEntity.ok(ApiResponse.ok("Employee retrieved successfully",
                employeeService.findEmployeeById(employeeId)));
    }
    @GetMapping("/list/all")
    public ResponseEntity<ApiResponse> getAllEmployees() {
        return ResponseEntity.ok(ApiResponse.ok("ALL employees retrieved successfully",
                employeeService.getAllEmployees()));
    }

    @PostMapping(value = "/save",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> saveEmployee(@ModelAttribute CreateEmployeeRequest requestEmployee) {
        return ResponseEntity.ok(ApiResponse.create("Employee saved successfully",
                employeeService.createEmployee(requestEmployee)));
    }
    @PostMapping(value = "/update/{employeeId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> updateEmployee(@PathVariable(name = "employeeId") Long employeeId,
                                                      @ModelAttribute CreateEmployeeRequest requestEmployee) {
        return ResponseEntity.ok(ApiResponse.create("Employee updated successfully",
                employeeService.updateEmployee(employeeId, requestEmployee)));
    }
    @DeleteMapping("/delete/{employeeId}")
    public ResponseEntity<ApiResponse> deleteEmployee(@PathVariable(name = "employeeId") Long employeeId) {
        employeeService.deleteEmployee(employeeId);
        return ResponseEntity.ok(ApiResponse.ok("Employee deleted successfully", true));
    }
}
