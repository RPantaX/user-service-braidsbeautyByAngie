package com.andres.springcloud.msvc.users.repositories.dao;

import com.andres.springcloud.msvc.users.dto.enums.EmployeeTypeEnum;
import com.andres.springcloud.msvc.users.entities.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeRepositoryCustom {
    Page<Employee> findAllPageableEmployeesWithRelationsByState(Pageable pageable, boolean state);
    // Nuevo método para filtrar por tipo de empleado
    Page<Employee> findActiveEmployeesByTypeWithRelations(Pageable pageable, Long employeeTypeId, boolean state);

    // Método alternativo usando enum
    Page<Employee> findActiveEmployeesByTypeWithRelations(Pageable pageable, EmployeeTypeEnum employeeType);

    // Método más flexible que acepta múltiples tipos
    Page<Employee> findActiveEmployeesByMultipleTypesWithRelations(Pageable pageable, Long... employeeTypeIds);

    // NUEVO: Método para filtrar por lista de IDs sin paginación
    List<Employee> findEmployeesByIdsWithRelations(List<Long> employeeIds);

    // NUEVO: Variante que solo trae empleados activos por IDs
    List<Employee> findActiveEmployeesByIdsWithRelations(List<Long> employeeIds);
    // NUEVO: Método para obtener TODOS los empleados sin filtros ni paginación
    List<Employee> findAllEmployeesWithRelations();
}