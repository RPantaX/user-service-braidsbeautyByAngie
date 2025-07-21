package com.andres.springcloud.msvc.users.services;

import com.andres.springcloud.msvc.users.dto.EmployeeDto;
import com.andres.springcloud.msvc.users.dto.EmployeeTypeDto;
import com.andres.springcloud.msvc.users.dto.enums.EmployeeTypeEnum;
import com.andres.springcloud.msvc.users.dto.request.CreateEmployeeRequest;
import com.andres.springcloud.msvc.users.dto.request.ResponseListPageableEmployee;

import java.util.List;

public interface IEmployeeService {
    //crud
    //create
    boolean createEmployee(CreateEmployeeRequest request);
    EmployeeDto findEmployeeById(Long employeeId);
    EmployeeDto findEmployeeByEmail(String email);
    boolean updateEmployee(Long employeeId, CreateEmployeeRequest request);
    void deleteEmployee(Long employeeId);
    ResponseListPageableEmployee listEmployeePageable(int pageNumber, int pageSize, String orderBy, String sortDir, boolean state);
    ResponseListPageableEmployee listEmployeePageableByType(int pageNumber, int pageSize, String orderBy, String sortDir, Long employeeTypeId, boolean state);
    ResponseListPageableEmployee listEmployeePageableByTypeEnum(int pageNumber, int pageSize, String orderBy, String sortDir, EmployeeTypeEnum employeeType);
    ResponseListPageableEmployee listEmployeePageableByMultipleTypes(int pageNumber, int pageSize, String orderBy, String sortDir, Long... employeeTypeIds);
    List<EmployeeDto> getEmployeesByIds(List<Long> employeeIds);
    List<EmployeeDto> getActiveEmployeesByIds(List<Long> employeeIds);
    List<EmployeeDto> getAllEmployees();
}
