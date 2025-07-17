package com.andres.springcloud.msvc.users.repositories.dao.impl;

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

import java.util.List;

@Repository
@Slf4j
public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Employee> findAllActiveEmployeesWithRelations(Pageable pageable) {
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
        Predicate activeCondition = cb.equal(employeeRoot.get("state"), true);
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

    private long countActiveEmployees() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Employee> countRoot = countQuery.from(Employee.class);

        countQuery.select(cb.count(countRoot));
        countQuery.where(cb.equal(countRoot.get("state"), true));

        return entityManager.createQuery(countQuery).getSingleResult();
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
