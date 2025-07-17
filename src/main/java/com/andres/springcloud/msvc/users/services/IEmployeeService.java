package com.andres.springcloud.msvc.users.services;

import com.andres.springcloud.msvc.users.dto.EmployeeDto;
import com.andres.springcloud.msvc.users.dto.request.CreateEmployeeRequest;
import com.andres.springcloud.msvc.users.dto.request.ResponseListPageableEmployee;

public interface IEmployeeService {
    //crud
    //create
    boolean createEmployee(CreateEmployeeRequest request);
    EmployeeDto findEmployeeById(Long employeeId);
    EmployeeDto findEmployeeByEmail(String email);
    boolean updateEmployee(Long employeeId, CreateEmployeeRequest request);
    void deleteEmployee(Long employeeId);
    ResponseListPageableEmployee listEmployeePageable(int pageNumber, int pageSize, String orderBy, String sortDir);
}
