package com.andres.springcloud.msvc.users.repositories.dao.impl;

import com.andres.springcloud.msvc.users.repositories.dao.CustomerRepositoryCustom;
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
public class CustomerRepositoryImpl implements CustomerRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Customer> findAllActiveCustomersWithRelations(Pageable pageable) {
        log.info("Executing criteria query for active customers with relations");

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Query principal para obtener los datos
        CriteriaQuery<Customer> query = cb.createQuery(Customer.class);
        Root<Customer> customerRoot = query.from(Customer.class);

        // JOIN FETCH para cargar todas las relaciones en una sola consulta
        Fetch<Customer, CustomerType> customerTypeFetch = customerRoot.fetch("customerType", JoinType.LEFT);
        Fetch<Customer, Person> personFetch = customerRoot.fetch("person", JoinType.LEFT);
        Fetch<Person, Address> addressFetch = personFetch.fetch("address", JoinType.LEFT);
        Fetch<Person, DocumentType> documentTypeFetch = personFetch.fetch("documentType", JoinType.LEFT);

        // Condición WHERE para clientes activos
        Predicate activeCondition = cb.equal(customerRoot.get("person").get("state"), true);
        query.where(activeCondition);

        // Aplicar ordenamiento
        if (pageable.getSort().isSorted()) {
            List<Order> orders = pageable.getSort().stream()
                    .map(sortOrder -> {
                        Path<Object> path = getPath(customerRoot, sortOrder.getProperty());
                        return sortOrder.isAscending() ?
                                cb.asc(path) : cb.desc(path);
                    })
                    .toList();
            query.orderBy(orders);
        } else {
            // Ordenamiento por defecto por ID
            query.orderBy(cb.desc(customerRoot.get("id")));
        }

        // Ejecutar query principal con paginación
        TypedQuery<Customer> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Customer> customers = typedQuery.getResultList();

        // Query para contar total de elementos
        long totalElements = countActiveCustomers();

        log.info("Retrieved {} customers out of {} total", customers.size(), totalElements);

        return new PageImpl<>(customers, pageable, totalElements);
    }

    private long countActiveCustomers() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Customer> countRoot = countQuery.from(Customer.class);

        countQuery.select(cb.count(countRoot));
        countQuery.where(cb.equal(countRoot.get("person").get("state"), true));

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private Path<Object> getPath(Root<Customer> root, String property) {
        // Manejo de propiedades anidadas para ordenamiento
        String[] parts = property.split("\\.");
        Path<Object> path = root.get(parts[0]);

        for (int i = 1; i < parts.length; i++) {
            path = path.get(parts[i]);
        }

        return path;
    }
}