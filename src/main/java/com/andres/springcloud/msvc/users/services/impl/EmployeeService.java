package com.andres.springcloud.msvc.users.services.impl;

import com.andres.springcloud.msvc.users.dto.*;
import com.andres.springcloud.msvc.users.dto.constants.Constants;
import com.andres.springcloud.msvc.users.dto.constants.UsersErrorEnum;
import com.andres.springcloud.msvc.users.dto.request.CreateEmployeeRequest;
import com.andres.springcloud.msvc.users.dto.request.ResponseListPageableEmployee;
import com.andres.springcloud.msvc.users.entities.*;
import com.andres.springcloud.msvc.users.repositories.DocumentTypeRepository;
import com.andres.springcloud.msvc.users.repositories.EmployeeRepository;
import com.andres.springcloud.msvc.users.repositories.EmployeeTypeRepository;
import com.andres.springcloud.msvc.users.repositories.PersonRepository;
import com.andres.springcloud.msvc.users.services.IEmployeeService;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.aws.IBucketUtil;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.util.BucketParams;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.util.ValidateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService implements IEmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeTypeRepository employeeTypeRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final PersonRepository personRepository;

    private final IBucketUtil bucketUtil;
    @Value("${BUCKET_NAME_USUARIOS}")
    private String bucketName;

    @Override
    public boolean createEmployee(CreateEmployeeRequest request) {
        log.info("Creating employee with request: {}", request);
        validateEmployeeData(request);
        Employee employee = createEmployeeInBD(request);
        Employee employeeSaved = employeeRepository.save(employee);
        if (request.getEmployeeImage() != null && !request.getEmployeeImage().isEmpty()) {
            String imageUrl = saveImageInS3(request.getEmployeeImage(), employeeSaved.getId());
            employeeSaved.setEmployeeImage(imageUrl);
            employeeRepository.save(employeeSaved);
            log.info("Image saved for employee ID: {}", employeeSaved.getId());
        }
        return true;
    }

    @Override
    public EmployeeDto findEmployeeById(Long employeeId) {
        log.info("Finding employee by ID: {}", employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElse(null);
        if (employee == null) {
            log.error("Employee with ID {} not found", employeeId);
            ValidateUtil.requerido(null, UsersErrorEnum.EMPLOYEE_NOT_FOUND_ERE00005, "Employee not found with ID: " + employeeId);
        }
        log.info("Employee found: {}", employee);
        return EmployeeEntityToDto(employee);
    }

    @Override
    public EmployeeDto findEmployeeByEmail(String email) {
        log.info("Finding employee by email: {}", email);
        Person person = personRepository.findByEmailAddress(email)
                .orElse(null);
        if (person == null) {
            log.error("Person with email {} not found", email);
            ValidateUtil.requerido(null, UsersErrorEnum.PERSON_NOT_FOUND_ERPE00001, "Person not found with email: " + email);
        }
        Employee employee = employeeRepository.findByPersonId(person.getId())
                .orElse(null);
        if (employee == null) {
            log.error("Employee with person ID {} not found", person.getId());
            ValidateUtil.requerido(null, UsersErrorEnum.EMPLOYEE_NOT_FOUND_ERE00005, "Employee not found for person ID: " + person.getId());
        }
        log.info("Employee found: {}", employee);
        return EmployeeEntityToDto(employee);
    }

    @Override
    public boolean updateEmployee(Long employeeId, CreateEmployeeRequest request) {
        log.info("Creating employee with request: {}", request);
        Employee employeeInBD = employeeRepository.findById(employeeId)
                .orElse(null);
        if (employeeInBD == null) {
            log.error("Employee with ID {} not found", employeeId);
            ValidateUtil.requerido(null, UsersErrorEnum.EMPLOYEE_NOT_FOUND_ERE00005, "Employee not found with ID: " + employeeId);
        }
        validateEmployeeDataUpdated(request, employeeInBD);
        Employee employee = updateEmployeeInBD(request, employeeInBD);
        Employee employeeSaved = employeeRepository.save(employee);
        if (request.getEmployeeImage() != null && !request.getEmployeeImage().isEmpty()) {
            String imageUrl = saveImageInS3(request.getEmployeeImage(), employeeSaved.getId());
            employeeSaved.setEmployeeImage(imageUrl);
            employeeRepository.save(employeeSaved);
            log.info("Image saved for employee ID: {}", employeeSaved.getId());
        }
        updateImageInS3(request.getEmployeeImage(), employeeSaved.getId(), employeeSaved, request.isDeleteFile());
        return true;
    }

    @Override
    public void deleteEmployee(Long employeeId) {
        log.info("Deleting employee with ID: {}", employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElse(null);
        if (employee == null) {
            log.error("Employee with ID {} not found", employeeId);
            ValidateUtil.requerido(null, UsersErrorEnum.EMPLOYEE_NOT_FOUND_ERE00005, "Employee not found with ID: " + employeeId);
        }

        // Eliminar imagen de S3 si existe
        if (employee.getEmployeeImage() != null && !employee.getEmployeeImage().isEmpty()) {
            Constants.deleteOldImageFromS3(employee.getEmployeeImage(), bucketUtil, bucketName);
            log.info("Image deleted for employee ID: {}", employeeId);
        }

        employeeRepository.delete(employee);
        log.info("Employee with ID {} deleted successfully", employeeId);
    }

    @Override
    public ResponseListPageableEmployee listEmployeePageable(int pageNumber, int pageSize, String orderBy, String sortDir) {
        log.info("Listing employees with parameters: pageNumber={}, pageSize={}, orderBy={}, sortDir={}",
                pageNumber, pageSize, orderBy, sortDir);

        // Crear Pageable con ordenamiento
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(orderBy).ascending() : Sort.by(orderBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        // Usar Criteria API para una sola consulta con JOIN FETCH
        Page<Employee> employeePage = employeeRepository.findAllActiveEmployeesWithRelations(pageable);

        // Mapear a DTOs (ahora sin consultas adicionales)
        List<EmployeeDto> employeeDtoList = employeePage.getContent().stream()
                .map(this::employeeEntityToDto)
                .toList();

        log.info("Retrieved {} employees from database in single query", employeeDtoList.size());

        return ResponseListPageableEmployee.builder()
                .employeeDtoList(employeeDtoList)
                .pageNumber(employeePage.getNumber())
                .pageSize(employeePage.getSize())
                .totalPages(employeePage.getTotalPages())
                .totalElements(employeePage.getTotalElements())
                .end(employeePage.isLast())
                .build();
    }

    // Método de mapeo optimizado
    private EmployeeDto employeeEntityToDto(Employee employee) {
        EmployeeTypeDto employeeTypeDto = null;
        if (employee.getEmployeeType() != null) {
            employeeTypeDto = EmployeeTypeDto.builder()
                    .id(employee.getEmployeeType().getId())
                    .value(employee.getEmployeeType().getValue())
                    .build();
        }

        AddressDto addressDto = null;
        if (employee.getPerson() != null && employee.getPerson().getAddress() != null) {
            Address address = employee.getPerson().getAddress();
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
        if (employee.getPerson() != null && employee.getPerson().getDocumentType() != null) {
            DocumentType docType = employee.getPerson().getDocumentType();
            documentTypeDto = DocumentTypeDto.builder()
                    .id(docType.getId())
                    .value(docType.getValue())
                    .build();
        }

        PersonDto personDto = null;
        if (employee.getPerson() != null) {
            Person person = employee.getPerson();
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

        UserDto userDto = null;
        if (employee.getUser() != null) {
            User user = employee.getUser();
            userDto = UserDto.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .enabled(user.getEnabled())
                    .build();
        }

        return EmployeeDto.builder()
                .id(employee.getId())
                .employeeImage(employee.getEmployeeImage())
                .employeeTypeId(employee.getEmployeeType() != null ? employee.getEmployeeType().getId() : null)
                .userId(employee.getUser() != null ? employee.getUser().getId() : null)
                .personId(employee.getPerson() != null ? employee.getPerson().getId() : null)
                .employeeType(employeeTypeDto)
                .user(userDto)
                .person(personDto)
                .employeeName(personDto != null ? personDto.getFullName() : null)
                .employeeEmail(personDto != null ? personDto.getEmailAddress() : null)
                .build();
    }

    private Employee createEmployeeInBD(CreateEmployeeRequest createEmployeeRequest) {
        Address address = Address.builder()
                .city(createEmployeeRequest.getCity().toUpperCase())
                .country(createEmployeeRequest.getCountry().toUpperCase())
                .postalCode(createEmployeeRequest.getPostalCode().toUpperCase())
                .street(createEmployeeRequest.getStreet().toUpperCase())
                .state(createEmployeeRequest.getState().toUpperCase())
                .description(createEmployeeRequest.getDescription().toUpperCase())
                .build();

        DocumentType documentType = documentTypeRepository.findById(createEmployeeRequest.getDocumentTypeId())
                .orElse(null);
        if(documentType == null) {
            log.error("Document type with ID {} not found", createEmployeeRequest.getDocumentTypeId());
            ValidateUtil.requerido(null, UsersErrorEnum.DOCUMENT_TYPE_NOT_FOUND_ERDT00002);
        }
        //we create the person
        Person person = Person.builder()
                .name(createEmployeeRequest.getName().toUpperCase())
                .lastName(createEmployeeRequest.getLastName().toUpperCase())
                .emailAddress(createEmployeeRequest.getEmailAddress().toUpperCase())
                .phoneNumber(createEmployeeRequest.getPhoneNumber())
                .address(address)
                .documentType(documentType)
                .state(Constants.STATUS_ACTIVE)
                .createdAt(Constants.getTimestamp())
                .modifiedByUser(!Constants.getUserInSession().isEmpty() ? Constants.getUserInSession() : "SYSTEM")
                .build();
        Person personSaved= personRepository.save(person);

        //emplouye type
        EmployeeType employeeType = employeeTypeRepository.findById(createEmployeeRequest.getEmployeeTypeId())
                .orElse(null);
        if(employeeType == null) {
            log.error("Employee type with ID {} not found", createEmployeeRequest.getEmployeeTypeId());
            ValidateUtil.requerido(null, UsersErrorEnum.EMPLOYEE_TYPE_NOT_FOUND_ERET00002);
        }

        //we create the employee
        return Employee.builder()
                .person(personSaved)
                .employeeType(employeeType)
                .state(Constants.STATUS_ACTIVE)
                .createdAt(Constants.getTimestamp())
                .modifiedByUser(!Constants.getUserInSession().isEmpty() ? Constants.getUserInSession() : "SYSTEM")
                .build();
    }
    private Employee updateEmployeeInBD(CreateEmployeeRequest createEmployeeRequest, Employee employeeInBD) {

        DocumentType documentType = documentTypeRepository.findById(createEmployeeRequest.getDocumentTypeId())
                .orElse(null);
        if(documentType == null) {
            log.error("Document type with ID {} not found", createEmployeeRequest.getDocumentTypeId());
            ValidateUtil.requerido(null, UsersErrorEnum.DOCUMENT_TYPE_NOT_FOUND_ERDT00002);
        }
        //we create the person
        employeeInBD.getPerson().getAddress().setCity(createEmployeeRequest.getCity().toUpperCase());
        employeeInBD.getPerson().getAddress().setCountry(createEmployeeRequest.getCountry().toUpperCase());
        employeeInBD.getPerson().getAddress().setPostalCode(createEmployeeRequest.getPostalCode().toUpperCase());
        employeeInBD.getPerson().getAddress().setStreet(createEmployeeRequest.getStreet().toUpperCase());
        employeeInBD.getPerson().getAddress().setState(createEmployeeRequest.getState().toUpperCase());
        employeeInBD.getPerson().getAddress().setDescription(createEmployeeRequest.getDescription().toUpperCase());
        employeeInBD.getPerson().setName(createEmployeeRequest.getName().toUpperCase());
        employeeInBD.getPerson().setLastName(createEmployeeRequest.getLastName().toUpperCase());
        employeeInBD.getPerson().setEmailAddress(createEmployeeRequest.getEmailAddress().toUpperCase());
        employeeInBD.getPerson().setPhoneNumber(createEmployeeRequest.getPhoneNumber());
        employeeInBD.getPerson().setDocumentType(documentType);
        employeeInBD.getPerson().setModifiedAt(Constants.getTimestamp());
        employeeInBD.getPerson().setModifiedByUser(!Constants.getUserInSession().isEmpty() ? Constants.getUserInSession() : "SYSTEM");

        //emplouye type
        EmployeeType employeeType = employeeTypeRepository.findById(createEmployeeRequest.getEmployeeTypeId())
                .orElse(null);
        if(employeeType == null) {
            log.error("Employee type with ID {} not found", createEmployeeRequest.getEmployeeTypeId());
            ValidateUtil.requerido(null, UsersErrorEnum.EMPLOYEE_TYPE_NOT_FOUND_ERET00002);
        }
        employeeInBD.setEmployeeType(employeeType);
        employeeInBD.setModifiedAt(Constants.getTimestamp());
        employeeInBD.setModifiedByUser(!Constants.getUserInSession().isEmpty() ? Constants.getUserInSession() : "SYSTEM");

        //we create the employee
        return employeeInBD;
    }
    private void validateEmployeeData(CreateEmployeeRequest request){
        Person person = personRepository.findByEmailAddress(request.getEmailAddress()).orElse(null);

        if (person != null) {
            log.error("Email address {} already exists", request.getEmailAddress());
            ValidateUtil.requerido(null,UsersErrorEnum.EMAIL_ALREADY_EXISTS_WAR00011);
        }

        Person person1 = personRepository.findByPhoneNumber(request.getPhoneNumber()).orElse(null);
        if (person1 != null) {
            log.error("Phone number {} already exists", request.getPhoneNumber());
            ValidateUtil.requerido(null,UsersErrorEnum.PHONE_ALREADY_EXISTS_WAR00012);
        }
    }
    private void validateEmployeeDataUpdated(CreateEmployeeRequest request, Employee employeeInBd){
        if (request.getEmailAddress() != null && !request.getEmailAddress().isEmpty() && !request.getEmailAddress().equals(employeeInBd.getPerson().getEmailAddress())) {
            Person person = personRepository.findByEmailAddress(request.getEmailAddress()).orElse(null);
            if (person != null ) {
                log.error("Email address {} already exists", request.getEmailAddress());
                ValidateUtil.requerido(null, UsersErrorEnum.EMAIL_ALREADY_EXISTS_WAR00011);
            }
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty() && !request.getPhoneNumber().equals(employeeInBd.getPerson().getPhoneNumber())) {
            Person person1 = personRepository.findByPhoneNumber(request.getPhoneNumber()).orElse(null);
            if (person1 != null) {
                log.error("Phone number {} already exists", request.getPhoneNumber());
                ValidateUtil.requerido(null, UsersErrorEnum.PHONE_ALREADY_EXISTS_WAR00012);
            }
        }
    }
    private String saveImageInS3(MultipartFile imagen, Long productId ) {
        BucketParams bucketParams = buildBucketParams(productId, imagen);
        bucketUtil.addFile(bucketParams);
        //bucketUtil.setPublic(bucketParams, true);
        return bucketUtil.getUrl(bucketParams);
    }
    public BucketParams buildBucketParams(Long employeeId, MultipartFile imagen){
        String fileName = "EMPLOYEE-" + employeeId + "-" + System.currentTimeMillis();

        // Para operaciones de eliminación (cuando imagen es null)
        if (imagen == null) {
            // Construir el path basado en el patrón de nombres que usamos
            String filePath = "employee/" + fileName; // Sin extensión para eliminación
            return BucketParams.builder()
                    .bucketName(bucketName)
                    .filePath(filePath)
                    .build();
        } else {
            // Para operaciones de creación/actualización
            String originalFileName = imagen.getOriginalFilename();
            if (originalFileName != null && originalFileName.contains(".")) {
                String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
                fileName += extension;
            }

            return BucketParams.builder()
                    .file(imagen)
                    .bucketName(bucketName)
                    .filePath("employee/" + fileName)
                    .build();
        }
    }
    private void updateImageInS3(MultipartFile newImage, Long employeeId, Employee employee, boolean deleteFile) {
        String currentImageUrl = employee.getEmployeeImage();

        if (deleteFile && currentImageUrl != null && !currentImageUrl.isEmpty()) {
            // Eliminar imagen existente
            Constants.deleteOldImageFromS3(currentImageUrl, bucketUtil, bucketName);
            employee.setEmployeeImage(null);
            employeeRepository.save(employee);
            log.info("Image deleted for employee ID: {}", employeeId);
            return;
        }

        if (newImage != null && !newImage.isEmpty()) {
            // Si ya existe una imagen, eliminarla primero
            if (currentImageUrl != null && !currentImageUrl.isEmpty()) {
                Constants.deleteOldImageFromS3(currentImageUrl, bucketUtil, bucketName);
                log.info("Old image replaced for employee ID: {}", employeeId);
            }

            // Guardar nueva imagen
            String newImageUrl = saveImageInS3(newImage, employeeId);
            employee.setEmployeeImage(newImageUrl);
            employeeRepository.save(employee);
            log.info("New image saved for employee ID: {}", employeeId);
        }
    }

    private EmployeeDto EmployeeEntityToDto(Employee employee){
        EmployeeTypeDto employeeTypeDto = EmployeeTypeDto.builder()
                .id(employee.getEmployeeType().getId())
                .value(employee.getEmployeeType().getValue()).build();
        AddressDto addressDto = AddressDto.builder()
                .city(employee.getPerson() != null && employee.getPerson().getAddress() != null ? employee.getPerson().getAddress().getCity() : null)
                .state(employee.getPerson() != null && employee.getPerson().getAddress() != null ? employee.getPerson().getAddress().getState() : null)
                .country(employee.getPerson() != null && employee.getPerson().getAddress() != null ? employee.getPerson().getAddress().getCountry() : null)
                .description(employee.getPerson() != null && employee.getPerson().getAddress() != null ? employee.getPerson().getAddress().getDescription() : null)
                .street(employee.getPerson() != null && employee.getPerson().getAddress() != null ? employee.getPerson().getAddress().getStreet() : null)
                .postalCode(employee.getPerson() != null && employee.getPerson().getAddress() != null ? employee.getPerson().getAddress().getPostalCode() : null)
                .id(employee.getPerson() != null && employee.getPerson().getAddress() != null ? employee.getPerson().getAddress().getId() : null)
                .build();
        DocumentTypeDto documentType = DocumentTypeDto
                .builder()
                .id(employee.getPerson() != null && employee.getPerson().getDocumentType() != null ? employee.getPerson().getDocumentType().getId() : null)
                .value(employee.getPerson() != null && employee.getPerson().getDocumentType() != null ? employee.getPerson().getDocumentType().getValue() : null)
                .build();
        PersonDto personDto = PersonDto.builder()
                .id(employee.getPerson() != null ? employee.getPerson().getId() : null)
                .name(employee.getPerson() != null ? employee.getPerson().getName() : null)
                .lastName(employee.getPerson() != null ? employee.getPerson().getLastName() : null)
                .emailAddress(employee.getPerson() != null ? employee.getPerson().getEmailAddress() : null)
                .phoneNumber(employee.getPerson() != null ? employee.getPerson().getPhoneNumber() : null)
                .address(addressDto)
                .documentType(documentType)
                .build();
        return EmployeeDto.builder()
                .id(employee.getId())
                .employeeType(employeeTypeDto)
                .person(personDto)
                .userId(employee.getUser() != null ? employee.getUser().getId() : null)
                .personId(employee.getPerson() != null ? employee.getPerson().getId() : null)
                .employeeImage(employee.getEmployeeImage())
                .build();
    }
}
