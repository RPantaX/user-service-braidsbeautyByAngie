package com.andres.springcloud.msvc.users.services.impl;

import com.andres.springcloud.msvc.users.dto.*;
import com.andres.springcloud.msvc.users.dto.constants.Constants;
import com.andres.springcloud.msvc.users.dto.constants.UsersErrorEnum;
import com.andres.springcloud.msvc.users.dto.request.CreateCustomerRequest;
import com.andres.springcloud.msvc.users.dto.request.ResponseListPageableCustomer;
import com.andres.springcloud.msvc.users.entities.*;
import com.andres.springcloud.msvc.users.repositories.CustomerRepository;
import com.andres.springcloud.msvc.users.repositories.CustomerTypeRepository;
import com.andres.springcloud.msvc.users.repositories.DocumentTypeRepository;
import com.andres.springcloud.msvc.users.repositories.PersonRepository;
import com.andres.springcloud.msvc.users.services.ICustomerService;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.util.ValidateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService implements ICustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerTypeRepository customerTypeRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final PersonRepository personRepository;

    @Override
    public CustomerDto createCustomer(CreateCustomerRequest createCustomerRequest) {
        log.info("Creating customer with request: {}", createCustomerRequest);
        validateCustomerData(createCustomerRequest);
        Customer customer = createCustomerInBD(createCustomerRequest);
        Customer customerSaved = customerRepository.save(customer);
        log.info("Customer created successfully with ID: {}", customerSaved.getId());
        return CustomerEntityToDto(customerSaved);
    }

    @Override
    public CustomerDto getCustomerById(Long customerId) {
        log.info("Finding customer by ID: {}", customerId);
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            log.error("Customer with ID {} not found", customerId);
            ValidateUtil.requerido(null, UsersErrorEnum.CUSTOMER_NOT_FOUND_ERC00008, "Customer not found with ID: " + customerId);
        }
        log.info("Customer found: {}", customer);
        return CustomerEntityToDto(customer);
    }

    @Override
    public CustomerDto updateCustomer(Long customerId, CreateCustomerRequest updateCustomerRequest) {
        log.info("Updating customer with ID {} and request: {}", customerId, updateCustomerRequest);
        Customer customerInBD = customerRepository.findById(customerId).orElse(null);
        if (customerInBD == null) {
            log.error("Customer with ID {} not found", customerId);
            ValidateUtil.requerido(null, UsersErrorEnum.CUSTOMER_NOT_FOUND_ERC00008, "Customer not found with ID: " + customerId);
        }
        validateCustomerDataUpdated(updateCustomerRequest, customerInBD);
        Customer customer = updateCustomerInBD(updateCustomerRequest, customerInBD);
        Customer customerSaved = customerRepository.save(customer);
        log.info("Customer updated successfully with ID: {}", customerSaved.getId());
        return CustomerEntityToDto(customerSaved);
    }

    @Override
    public void deleteCustomer(Long customerId) {
        log.info("Deleting customer with ID: {}", customerId);
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            log.error("Customer with ID {} not found", customerId);
            ValidateUtil.requerido(null, UsersErrorEnum.CUSTOMER_NOT_FOUND_ERC00008, "Customer not found with ID: " + customerId);
        }

        customerRepository.delete(customer);
        log.info("Customer with ID {} deleted successfully", customerId);
    }

    @Override
    public CustomerDto getCustomerByEmail(String email) {
        log.info("Finding customer by email: {}", email);
        Person person = personRepository.findByEmailAddress(email).orElse(null);
        if (person == null) {
            log.error("Person with email {} not found", email);
            ValidateUtil.requerido(null, UsersErrorEnum.PERSON_NOT_FOUND_ERPE00001, "Person not found with email: " + email);
        }
        Customer customer = customerRepository.findByPersonId(person.getId()).orElse(null);
        if (customer == null) {
            log.error("Customer with person ID {} not found", person.getId());
            ValidateUtil.requerido(null, UsersErrorEnum.CUSTOMER_NOT_FOUND_ERC00008, "Customer not found for person ID: " + person.getId());
        }
        log.info("Customer found: {}", customer);
        return CustomerEntityToDto(customer);
    }

    @Override
    public ResponseListPageableCustomer getAllCustomersPageable(int pageNumber, int pageSize, String orderBy, String sortDir) {
        log.info("Listing customers with parameters: pageNumber={}, pageSize={}, orderBy={}, sortDir={}",
                pageNumber, pageSize, orderBy, sortDir);

        // Crear Pageable con ordenamiento
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(orderBy).ascending() : Sort.by(orderBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        // Usar Criteria API para una sola consulta con JOIN FETCH
        Page<Customer> customerPage = customerRepository.findAllActiveCustomersWithRelations(pageable);

        // Mapear a DTOs (ahora sin consultas adicionales)
        List<CustomerDto> customerDtoList = customerPage.getContent().stream()
                .map(this::customerEntityToDto)
                .toList();

        log.info("Retrieved {} customers from database in single query", customerDtoList.size());

        return ResponseListPageableCustomer.builder()
                .customerDtoList(customerDtoList)
                .pageNumber(customerPage.getNumber())
                .pageSize(customerPage.getSize())
                .totalPages(customerPage.getTotalPages())
                .totalElements(customerPage.getTotalElements())
                .end(customerPage.isLast())
                .build();
    }

    // Método de mapeo optimizado (similar al EmployeeEntityToDto)
    private CustomerDto customerEntityToDto(Customer customer) {
        CustomerTypeDto customerTypeDto = null;
        if (customer.getCustomerType() != null) {
            customerTypeDto = CustomerTypeDto.builder()
                    .id(customer.getCustomerType().getId())
                    .value(customer.getCustomerType().getValue())
                    .build();
        }

        AddressDto addressDto = null;
        if (customer.getPerson() != null && customer.getPerson().getAddress() != null) {
            Address address = customer.getPerson().getAddress();
            addressDto = AddressDto.builder()
                    .id(address.getId())
                    .city(address.getCity())
                    .state(address.getState())
                    .country(address.getCountry())
                    .street(address.getStreet())
                    .postalCode(address.getPostalCode())
                    .build();
        }

        DocumentTypeDto documentTypeDto = null;
        if (customer.getPerson() != null && customer.getPerson().getDocumentType() != null) {
            DocumentType docType = customer.getPerson().getDocumentType();
            documentTypeDto = DocumentTypeDto.builder()
                    .id(docType.getId())
                    .value(docType.getValue())
                    .build();
        }

        PersonDto personDto = null;
        if (customer.getPerson() != null) {
            Person person = customer.getPerson();
            personDto = PersonDto.builder()
                    .id(person.getId())
                    .name(person.getName())
                    .lastName(person.getLastName())
                    .emailAddress(person.getEmailAddress())
                    .phoneNumber(person.getPhoneNumber())
                    .address(addressDto)
                    .documentType(documentTypeDto)
                    .build();
        }

        return CustomerDto.builder()
                .id(customer.getId())
                .phoneWhatsapp(customer.getPhoneWhatsapp())
                .customerTypeId(customer.getCustomerType() != null ? customer.getCustomerType().getId() : null)
                .personId(customer.getPerson() != null ? customer.getPerson().getId() : null)
                .customerType(customerTypeDto)
                .person(personDto)
                .customerName(personDto != null ? personDto.getFullName() : null)
                .customerEmail(personDto != null ? personDto.getEmailAddress() : null)
                .primaryPhone(customer.getPhoneWhatsapp() != null && !customer.getPhoneWhatsapp().isEmpty()
                        ? customer.getPhoneWhatsapp()
                        : (personDto != null ? personDto.getPhoneNumber() : null))
                .build();
    }

    // ================= MÉTODOS PRIVADOS (SIMILARES A EMPLOYEE SERVICE) =================

    private Customer createCustomerInBD(CreateCustomerRequest createCustomerRequest) {
        // Crear dirección
        Address address = Address.builder()
                .city(createCustomerRequest.getCity().toUpperCase())
                .country(createCustomerRequest.getCountry().toUpperCase())
                .postalCode(createCustomerRequest.getPostalCode().toUpperCase())
                .street(createCustomerRequest.getStreet().toUpperCase())
                .description(createCustomerRequest.getAddressDescription() != null ? createCustomerRequest.getAddressDescription().toUpperCase() : "NA")
                .state(createCustomerRequest.getState().toUpperCase())
                .build();

        // Buscar tipo de documento
        DocumentType documentType = documentTypeRepository.findById(createCustomerRequest.getDocumentTypeId())
                .orElse(null);
        if (documentType == null) {
            log.error("Document type with ID {} not found", createCustomerRequest.getDocumentTypeId());
            ValidateUtil.requerido(null, UsersErrorEnum.DOCUMENT_TYPE_NOT_FOUND_ERDT00002);
        }

        // Crear persona
        Person person = Person.builder()
                .name(createCustomerRequest.getName().toUpperCase())
                .lastName(createCustomerRequest.getLastName().toUpperCase())
                .emailAddress(createCustomerRequest.getEmailAddress().toUpperCase())
                .phoneNumber(createCustomerRequest.getPhoneNumber())
                .address(address)
                .documentType(documentType)
                .documentNumber(createCustomerRequest.getDocumentNumber().toUpperCase())
                .state(Constants.STATUS_ACTIVE)
                .createdAt(Constants.getTimestamp())
                .modifiedByUser(!Constants.getUserInSession().isEmpty() ? Constants.getUserInSession() : "SYSTEM")
                .build();
        Person personSaved = personRepository.save(person);

        // Buscar tipo de cliente
        CustomerType customerType = customerTypeRepository.findById(createCustomerRequest.getCustomerTypeId())
                .orElse(null);
        if (customerType == null) {
            log.error("Customer type with ID {} not found", createCustomerRequest.getCustomerTypeId());
            ValidateUtil.requerido(null, UsersErrorEnum.CUSTOMER_TYPE_NOT_FOUND_ERCT00002);
        }

        // Crear cliente
        return Customer.builder()
                .person(personSaved)
                .customerType(customerType)
                .phoneWhatsapp(createCustomerRequest.getPhoneWhatsapp())
                .state(Constants.STATUS_ACTIVE)
                .createdAt(Constants.getTimestamp())
                .modifiedByUser(!Constants.getUserInSession().isEmpty() ? Constants.getUserInSession() : "SYSTEM")
                .build();
    }

    private Customer updateCustomerInBD(CreateCustomerRequest createCustomerRequest, Customer customerInBD) {
        // Buscar tipo de documento
        DocumentType documentType = documentTypeRepository.findById(createCustomerRequest.getDocumentTypeId())
                .orElse(null);
        if (documentType == null) {
            log.error("Document type with ID {} not found", createCustomerRequest.getDocumentTypeId());
            ValidateUtil.requerido(null, UsersErrorEnum.DOCUMENT_TYPE_NOT_FOUND_ERDT00002);
        }

        // Actualizar dirección
        customerInBD.getPerson().getAddress().setCity(createCustomerRequest.getCity().toUpperCase());
        customerInBD.getPerson().getAddress().setCountry(createCustomerRequest.getCountry().toUpperCase());
        customerInBD.getPerson().getAddress().setPostalCode(createCustomerRequest.getPostalCode().toUpperCase());
        customerInBD.getPerson().getAddress().setStreet(createCustomerRequest.getStreet().toUpperCase());
        customerInBD.getPerson().getAddress().setState(createCustomerRequest.getState().toUpperCase());

        // Actualizar persona
        customerInBD.getPerson().setName(createCustomerRequest.getName().toUpperCase());
        customerInBD.getPerson().setLastName(createCustomerRequest.getLastName().toUpperCase());
        customerInBD.getPerson().setEmailAddress(createCustomerRequest.getEmailAddress().toUpperCase());
        customerInBD.getPerson().setPhoneNumber(createCustomerRequest.getPhoneNumber());
        customerInBD.getPerson().setDocumentType(documentType);
        customerInBD.getPerson().setModifiedAt(Constants.getTimestamp());
        customerInBD.getPerson().setModifiedByUser(!Constants.getUserInSession().isEmpty() ? Constants.getUserInSession() : "SYSTEM");

        // Buscar tipo de cliente
        CustomerType customerType = customerTypeRepository.findById(createCustomerRequest.getCustomerTypeId())
                .orElse(null);
        if (customerType == null) {
            log.error("Customer type with ID {} not found", createCustomerRequest.getCustomerTypeId());
            ValidateUtil.requerido(null, UsersErrorEnum.CUSTOMER_TYPE_NOT_FOUND_ERCT00002);
        }

        // Actualizar cliente
        customerInBD.setCustomerType(customerType);
        customerInBD.setPhoneWhatsapp(createCustomerRequest.getPhoneWhatsapp());

        return customerInBD;
    }

    private void validateCustomerData(CreateCustomerRequest request) {
        Person person = personRepository.findByEmailAddress(request.getEmailAddress()).orElse(null);
        if (person != null) {
            log.error("Email address {} already exists", request.getEmailAddress());
            ValidateUtil.requerido(null, UsersErrorEnum.EMAIL_ALREADY_EXISTS_WAR00011);
        }

        Person person1 = personRepository.findByPhoneNumber(request.getPhoneNumber()).orElse(null);
        if (person1 != null) {
            log.error("Phone number {} already exists", request.getPhoneNumber());
            ValidateUtil.requerido(null, UsersErrorEnum.PHONE_ALREADY_EXISTS_WAR00012);
        }
    }

    private void validateCustomerDataUpdated(CreateCustomerRequest request, Customer customerInBd) {
        if (request.getEmailAddress() != null && !request.getEmailAddress().isEmpty()
                && !request.getEmailAddress().equals(customerInBd.getPerson().getEmailAddress())) {
            Person person = personRepository.findByEmailAddress(request.getEmailAddress()).orElse(null);
            if (person != null) {
                log.error("Email address {} already exists", request.getEmailAddress());
                ValidateUtil.requerido(null, UsersErrorEnum.EMAIL_ALREADY_EXISTS_WAR00011);
            }
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()
                && !request.getPhoneNumber().equals(customerInBd.getPerson().getPhoneNumber())) {
            Person person1 = personRepository.findByPhoneNumber(request.getPhoneNumber()).orElse(null);
            if (person1 != null) {
                log.error("Phone number {} already exists", request.getPhoneNumber());
                ValidateUtil.requerido(null, UsersErrorEnum.PHONE_ALREADY_EXISTS_WAR00012);
            }
        }
    }

    // Método original para compatibilidad (similar al EmployeeEntityToDto del service original)
    private CustomerDto CustomerEntityToDto(Customer customer) {
        CustomerTypeDto customerTypeDto = CustomerTypeDto.builder()
                .id(customer.getCustomerType().getId())
                .value(customer.getCustomerType().getValue()).build();

        AddressDto addressDto = AddressDto.builder()
                .city(customer.getPerson() != null && customer.getPerson().getAddress() != null ? customer.getPerson().getAddress().getCity() : null)
                .state(customer.getPerson() != null && customer.getPerson().getAddress() != null ? customer.getPerson().getAddress().getState() : null)
                .country(customer.getPerson() != null && customer.getPerson().getAddress() != null ? customer.getPerson().getAddress().getCountry() : null)
                .street(customer.getPerson() != null && customer.getPerson().getAddress() != null ? customer.getPerson().getAddress().getStreet() : null)
                .postalCode(customer.getPerson() != null && customer.getPerson().getAddress() != null ? customer.getPerson().getAddress().getPostalCode() : null)
                .id(customer.getPerson() != null && customer.getPerson().getAddress() != null ? customer.getPerson().getAddress().getId() : null)
                .build();

        DocumentTypeDto documentType = DocumentTypeDto
                .builder()
                .id(customer.getPerson() != null && customer.getPerson().getDocumentType() != null ? customer.getPerson().getDocumentType().getId() : null)
                .value(customer.getPerson() != null && customer.getPerson().getDocumentType() != null ? customer.getPerson().getDocumentType().getValue() : null)
                .build();

        PersonDto personDto = PersonDto.builder()
                .id(customer.getPerson() != null ? customer.getPerson().getId() : null)
                .name(customer.getPerson() != null ? customer.getPerson().getName() : null)
                .lastName(customer.getPerson() != null ? customer.getPerson().getLastName() : null)
                .emailAddress(customer.getPerson() != null ? customer.getPerson().getEmailAddress() : null)
                .phoneNumber(customer.getPerson() != null ? customer.getPerson().getPhoneNumber() : null)
                .address(addressDto)
                .documentType(documentType)
                .build();

        return CustomerDto.builder()
                .id(customer.getId())
                .customerType(customerTypeDto)
                .person(personDto)
                .customerTypeId(customer.getCustomerType() != null ? customer.getCustomerType().getId() : null)
                .personId(customer.getPerson() != null ? customer.getPerson().getId() : null)
                .phoneWhatsapp(customer.getPhoneWhatsapp())
                .build();
    }
}