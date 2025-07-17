package com.andres.springcloud.msvc.users.controllers;

import com.andres.springcloud.msvc.users.dto.request.CreateEmployeeRequest;
import com.andres.springcloud.msvc.users.services.IEmployeeService;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.Constants;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user-service/employee")
@RequiredArgsConstructor
public class EmployeeController {

    IEmployeeService employeeService;

    @GetMapping("/list/pageable")
    public ResponseEntity<ApiResponse> listEmployeePageableList(@RequestParam(value = "pageNo", defaultValue = Constants.NUM_PAG_BY_DEFECT, required = false) int pageNo,
                                                                @RequestParam(value = "pageSize", defaultValue = Constants.SIZE_PAG_BY_DEFECT, required = false) int pageSize,
                                                                @RequestParam(value = "sortBy", defaultValue = Constants.ORDER_BY_DEFECT_ALL, required = false) String sortBy,
                                                                @RequestParam(value = "sortDir", defaultValue = Constants.ORDER_DIRECT_BY_DEFECT, required = false) String sortDir) {
        return ResponseEntity.ok(ApiResponse.ok("List of employees retrieved successfully",
                employeeService.listEmployeePageable(pageNo, pageSize, sortBy, sortDir)));
    }
    @GetMapping("/findById/{employeeId}")
    public ResponseEntity<ApiResponse> findEmployeeById(@PathVariable(name = "employeeId") Long employeeId) {
        return ResponseEntity.ok(ApiResponse.ok("Employee retrieved successfully",
                employeeService.findEmployeeById(employeeId)));
    }
    @PostMapping("/save")
    public ResponseEntity<ApiResponse> saveEmployee(@RequestBody CreateEmployeeRequest requestEmployee) {
        return ResponseEntity.ok(ApiResponse.create("Employee saved successfully",
                employeeService.createEmployee(requestEmployee)));
    }
    @PutMapping("/update/{employeeId}")
    public ResponseEntity<ApiResponse> updateEmployee(@PathVariable(name = "employeeId") Long employeeId,
                                                      @RequestBody CreateEmployeeRequest requestEmployee) {
        return ResponseEntity.ok(ApiResponse.create("Employee updated successfully",
                employeeService.updateEmployee(employeeId, requestEmployee)));
    }
    @DeleteMapping("/delete/{employeeId}")
    public ResponseEntity<ApiResponse> deleteEmployee(@PathVariable(name = "employeeId") Long employeeId) {
        employeeService.deleteEmployee(employeeId);
        return ResponseEntity.ok(ApiResponse.ok("Employee deleted successfully", true));
    }
}
