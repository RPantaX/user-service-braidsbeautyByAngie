package com.andres.springcloud.msvc.users.repositories.dao.impl;

import com.andres.springcloud.msvc.users.dto.enums.EmployeeTypeEnum;
import com.andres.springcloud.msvc.users.repositories.dao.EmployeeRepositoryCustom;
import com.andres.springcloud.msvc.users.entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
@Slf4j
public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Employee> findAllPageableEmployeesWithRelationsByState(Pageable pageable, boolean state) {
        log.info("Executing criteria query for active employees with relations");

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Query principal para obtener los datos
        CriteriaQuery<Employee> query = cb.createQuery(Employee.class);
        Root<Employee> employeeRoot = query.from(Employee.class);

        // JOIN FETCH para cargar todas las relaciones en una sola consulta
        Fetch<Employee, EmployeeType> employeeTypeFetch = employeeRoot.fetch("employeeType", JoinType.LEFT);
        Fetch<Employee, Person> personFetch = employeeRoot.fetch("person", JoinType.LEFT);
        Fetch<Person, Address> addressFetch = personFetch.fetch("address", JoinType.LEFT);
        Fetch<Person, DocumentType> documentTypeFetch = personFetch.fetch("documentType", JoinType.LEFT);

        // JOIN FETCH opcional para User si es necesario
        Fetch<Employee, User> userFetch = employeeRoot.fetch("user", JoinType.LEFT);

        // Condición WHERE para empleados activos
        Predicate activeCondition = cb.equal(employeeRoot.get("state"), state);
        query.where(activeCondition);

        // Aplicar ordenamiento
        if (pageable.getSort().isSorted()) {
            List<Order> orders = pageable.getSort().stream()
                    .map(sortOrder -> {
                        Path<Object> path = getPath(employeeRoot, sortOrder.getProperty());
                        return sortOrder.isAscending() ?
                                cb.asc(path) : cb.desc(path);
                    })
                    .toList();
            query.orderBy(orders);
        } else {
            // Ordenamiento por defecto por ID
            query.orderBy(cb.desc(employeeRoot.get("id")));
        }

        // Ejecutar query principal con paginación
        TypedQuery<Employee> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Employee> employees = typedQuery.getResultList();

        // Query para contar total de elementos
        long totalElements = countActiveEmployees();

        log.info("Retrieved {} employees out of {} total", employees.size(), totalElements);

        return new PageImpl<>(employees, pageable, totalElements);
    }

    @Override
    public Page<Employee> findActiveEmployeesByTypeWithRelations(Pageable pageable, Long employeeTypeId, boolean state) {
        log.info("Executing criteria query for active employees with type ID {} and relations", employeeTypeId);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Query principal para obtener los datos
        CriteriaQuery<Employee> query = cb.createQuery(Employee.class);
        Root<Employee> employeeRoot = query.from(Employee.class);

        // JOIN FETCH para cargar todas las relaciones en una sola consulta
        Fetch<Employee, EmployeeType> employeeTypeFetch = employeeRoot.fetch("employeeType", JoinType.LEFT);
        Fetch<Employee, Person> personFetch = employeeRoot.fetch("person", JoinType.LEFT);
        Fetch<Person, Address> addressFetch = personFetch.fetch("address", JoinType.LEFT);
        Fetch<Person, DocumentType> documentTypeFetch = personFetch.fetch("documentType", JoinType.LEFT);

        // JOIN FETCH opcional para User si es necesario
        Fetch<Employee, User> userFetch = employeeRoot.fetch("user", JoinType.LEFT);

        // Condiciones WHERE
        List<Predicate> predicates = new ArrayList<>();

        // Empleados activos
        predicates.add(cb.equal(employeeRoot.get("state"), state));

        // Filtro por tipo de empleado
        if (employeeTypeId != null) {
            predicates.add(cb.equal(employeeRoot.get("employeeType").get("id"), employeeTypeId));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Aplicar ordenamiento
        applyOrdering(cb, query, employeeRoot, pageable);

        // Ejecutar query principal con paginación
        TypedQuery<Employee> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Employee> employees = typedQuery.getResultList();

        // Query para contar total de elementos con el mismo filtro
        long totalElements = countActiveEmployeesByType(employeeTypeId);

        log.info("Retrieved {} employees of type {} out of {} total", employees.size(), employeeTypeId, totalElements);

        return new PageImpl<>(employees, pageable, totalElements);
    }

    @Override
    public Page<Employee> findActiveEmployeesByTypeWithRelations(Pageable pageable, EmployeeTypeEnum employeeType) {
        log.info("Executing criteria query for active employees with type {} and relations", employeeType);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Query principal para obtener los datos
        CriteriaQuery<Employee> query = cb.createQuery(Employee.class);
        Root<Employee> employeeRoot = query.from(Employee.class);

        // JOIN FETCH para cargar todas las relaciones en una sola consulta
        Fetch<Employee, EmployeeType> employeeTypeFetch = employeeRoot.fetch("employeeType", JoinType.LEFT);
        Fetch<Employee, Person> personFetch = employeeRoot.fetch("person", JoinType.LEFT);
        Fetch<Person, Address> addressFetch = personFetch.fetch("address", JoinType.LEFT);
        Fetch<Person, DocumentType> documentTypeFetch = personFetch.fetch("documentType", JoinType.LEFT);

        // JOIN FETCH opcional para User si es necesario
        Fetch<Employee, User> userFetch = employeeRoot.fetch("user", JoinType.LEFT);

        // Condiciones WHERE
        List<Predicate> predicates = new ArrayList<>();

        // Empleados activos
        predicates.add(cb.equal(employeeRoot.get("state"), true));

        // Filtro por enum de tipo de empleado (asumiendo que coincide con el value)
        if (employeeType != null) {
            predicates.add(cb.equal(employeeRoot.get("employeeType").get("value"), employeeType.name()));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Aplicar ordenamiento
        applyOrdering(cb, query, employeeRoot, pageable);

        // Ejecutar query principal con paginación
        TypedQuery<Employee> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Employee> employees = typedQuery.getResultList();

        // Query para contar total de elementos con el mismo filtro
        long totalElements = countActiveEmployeesByTypeEnum(employeeType);

        log.info("Retrieved {} employees of type {} out of {} total", employees.size(), employeeType, totalElements);

        return new PageImpl<>(employees, pageable, totalElements);
    }

    @Override
    public Page<Employee> findActiveEmployeesByMultipleTypesWithRelations(Pageable pageable, Long... employeeTypeIds) {
        log.info("Executing criteria query for active employees with types {} and relations", Arrays.toString(employeeTypeIds));

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Query principal para obtener los datos
        CriteriaQuery<Employee> query = cb.createQuery(Employee.class);
        Root<Employee> employeeRoot = query.from(Employee.class);

        // JOIN FETCH para cargar todas las relaciones en una sola consulta
        Fetch<Employee, EmployeeType> employeeTypeFetch = employeeRoot.fetch("employeeType", JoinType.LEFT);
        Fetch<Employee, Person> personFetch = employeeRoot.fetch("person", JoinType.LEFT);
        Fetch<Person, Address> addressFetch = personFetch.fetch("address", JoinType.LEFT);
        Fetch<Person, DocumentType> documentTypeFetch = personFetch.fetch("documentType", JoinType.LEFT);

        // JOIN FETCH opcional para User si es necesario
        Fetch<Employee, User> userFetch = employeeRoot.fetch("user", JoinType.LEFT);

        // Condiciones WHERE
        List<Predicate> predicates = new ArrayList<>();

        // Empleados activos
        predicates.add(cb.equal(employeeRoot.get("state"), true));

        // Filtro por múltiples tipos de empleado
        if (employeeTypeIds != null && employeeTypeIds.length > 0) {
            List<Long> typeIdsList = Arrays.asList(employeeTypeIds);
            predicates.add(employeeRoot.get("employeeType").get("id").in(typeIdsList));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Aplicar ordenamiento
        applyOrdering(cb, query, employeeRoot, pageable);

        // Ejecutar query principal con paginación
        TypedQuery<Employee> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Employee> employees = typedQuery.getResultList();

        // Query para contar total de elementos con el mismo filtro
        long totalElements = countActiveEmployeesByMultipleTypes(employeeTypeIds);

        log.info("Retrieved {} employees of types {} out of {} total", employees.size(), Arrays.toString(employeeTypeIds), totalElements);

        return new PageImpl<>(employees, pageable, totalElements);
    }

    // ================= MÉTODOS AUXILIARES =================

    private void applyOrdering(CriteriaBuilder cb, CriteriaQuery<Employee> query, Root<Employee> employeeRoot, Pageable pageable) {
        if (pageable.getSort().isSorted()) {
            List<Order> orders = pageable.getSort().stream()
                    .map(sortOrder -> {
                        Path<Object> path = getPath(employeeRoot, sortOrder.getProperty());
                        return sortOrder.isAscending() ?
                                cb.asc(path) : cb.desc(path);
                    })
                    .toList();
            query.orderBy(orders);
        } else {
            // Ordenamiento por defecto por ID
            query.orderBy(cb.desc(employeeRoot.get("id")));
        }
    }

    private long countActiveEmployees() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Employee> countRoot = countQuery.from(Employee.class);

        countQuery.select(cb.count(countRoot));
        countQuery.where(cb.equal(countRoot.get("state"), true));

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private long countActiveEmployeesByType(Long employeeTypeId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Employee> countRoot = countQuery.from(Employee.class);

        countQuery.select(cb.count(countRoot));

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(countRoot.get("state"), true));

        if (employeeTypeId != null) {
            predicates.add(cb.equal(countRoot.get("employeeType").get("id"), employeeTypeId));
        }

        countQuery.where(cb.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private long countActiveEmployeesByTypeEnum(EmployeeTypeEnum employeeType) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Employee> countRoot = countQuery.from(Employee.class);

        countQuery.select(cb.count(countRoot));

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(countRoot.get("state"), true));

        if (employeeType != null) {
            predicates.add(cb.equal(countRoot.get("employeeType").get("value"), employeeType.name()));
        }

        countQuery.where(cb.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private long countActiveEmployeesByMultipleTypes(Long... employeeTypeIds) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Employee> countRoot = countQuery.from(Employee.class);

        countQuery.select(cb.count(countRoot));

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(countRoot.get("state"), true));

        if (employeeTypeIds != null && employeeTypeIds.length > 0) {
            List<Long> typeIdsList = Arrays.asList(employeeTypeIds);
            predicates.add(countRoot.get("employeeType").get("id").in(typeIdsList));
        }

        countQuery.where(cb.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    @Override
    public List<Employee> findEmployeesByIdsWithRelations(List<Long> employeeIds) {
        log.info("Executing criteria query for employees with IDs {} and relations", employeeIds);

        if (employeeIds == null || employeeIds.isEmpty()) {
            log.warn("Employee IDs list is null or empty, returning empty list");
            return new ArrayList<>();
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Query principal para obtener los datos
        CriteriaQuery<Employee> query = cb.createQuery(Employee.class);
        Root<Employee> employeeRoot = query.from(Employee.class);

        // JOIN FETCH para cargar todas las relaciones en una sola consulta
        Fetch<Employee, EmployeeType> employeeTypeFetch = employeeRoot.fetch("employeeType", JoinType.LEFT);
        Fetch<Employee, Person> personFetch = employeeRoot.fetch("person", JoinType.LEFT);
        Fetch<Person, Address> addressFetch = personFetch.fetch("address", JoinType.LEFT);
        Fetch<Person, DocumentType> documentTypeFetch = personFetch.fetch("documentType", JoinType.LEFT);

        // JOIN FETCH opcional para User si es necesario
        Fetch<Employee, User> userFetch = employeeRoot.fetch("user", JoinType.LEFT);

        // Condición WHERE para filtrar por IDs
        query.where(employeeRoot.get("id").in(employeeIds));

        // Ordenamiento por ID para consistencia
        query.orderBy(cb.asc(employeeRoot.get("id")));

        // Ejecutar query sin paginación
        TypedQuery<Employee> typedQuery = entityManager.createQuery(query);
        List<Employee> employees = typedQuery.getResultList();

        log.info("Retrieved {} employees for {} requested IDs", employees.size(), employeeIds.size());

        return employees;
    }

    @Override
    public List<Employee> findActiveEmployeesByIdsWithRelations(List<Long> employeeIds) {
        log.info("Executing criteria query for active employees with IDs {} and relations", employeeIds);

        if (employeeIds == null || employeeIds.isEmpty()) {
            log.warn("Employee IDs list is null or empty, returning empty list");
            return new ArrayList<>();
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Query principal para obtener los datos
        CriteriaQuery<Employee> query = cb.createQuery(Employee.class);
        Root<Employee> employeeRoot = query.from(Employee.class);

        // JOIN FETCH para cargar todas las relaciones en una sola consulta
        Fetch<Employee, EmployeeType> employeeTypeFetch = employeeRoot.fetch("employeeType", JoinType.LEFT);
        Fetch<Employee, Person> personFetch = employeeRoot.fetch("person", JoinType.LEFT);
        Fetch<Person, Address> addressFetch = personFetch.fetch("address", JoinType.LEFT);
        Fetch<Person, DocumentType> documentTypeFetch = personFetch.fetch("documentType", JoinType.LEFT);

        // JOIN FETCH opcional para User si es necesario
        Fetch<Employee, User> userFetch = employeeRoot.fetch("user", JoinType.LEFT);

        // Condiciones WHERE
        List<Predicate> predicates = new ArrayList<>();

        // Filtrar por IDs
        predicates.add(employeeRoot.get("id").in(employeeIds));

        // Solo empleados activos
        predicates.add(cb.equal(employeeRoot.get("state"), true));

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Ordenamiento por ID para consistencia
        query.orderBy(cb.asc(employeeRoot.get("id")));

        // Ejecutar query sin paginación
        TypedQuery<Employee> typedQuery = entityManager.createQuery(query);
        List<Employee> employees = typedQuery.getResultList();

        log.info("Retrieved {} active employees for {} requested IDs", employees.size(), employeeIds.size());

        return employees;
    }
    @Override
    public List<Employee> findAllEmployeesWithRelations() {
        log.info("Executing criteria query for ALL employees with relations (no filters)");

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Query principal para obtener los datos
        CriteriaQuery<Employee> query = cb.createQuery(Employee.class);
        Root<Employee> employeeRoot = query.from(Employee.class);

        // JOIN FETCH para cargar todas las relaciones en una sola consulta
        Fetch<Employee, EmployeeType> employeeTypeFetch = employeeRoot.fetch("employeeType", JoinType.LEFT);
        Fetch<Employee, Person> personFetch = employeeRoot.fetch("person", JoinType.LEFT);
        Fetch<Person, Address> addressFetch = personFetch.fetch("address", JoinType.LEFT);
        Fetch<Person, DocumentType> documentTypeFetch = personFetch.fetch("documentType", JoinType.LEFT);

        // JOIN FETCH opcional para User si es necesario
        Fetch<Employee, User> userFetch = employeeRoot.fetch("user", JoinType.LEFT);

        // SIN FILTROS - Traer todos los empleados (activos e inactivos)

        // Ordenamiento por ID para consistencia
        query.orderBy(cb.asc(employeeRoot.get("id")));

        // Ejecutar query sin paginación ni filtros
        TypedQuery<Employee> typedQuery = entityManager.createQuery(query);
        List<Employee> employees = typedQuery.getResultList();

        log.info("Retrieved ALL {} employees with relations (no filters applied)", employees.size());

        return employees;
    }
    private Path<Object> getPath(Root<Employee> root, String property) {
        // Manejo de propiedades anidadas para ordenamiento
        String[] parts = property.split("\\.");
        Path<Object> path = root.get(parts[0]);

        for (int i = 1; i < parts.length; i++) {
            path = path.get(parts[i]);
        }

        return path;
    }
}