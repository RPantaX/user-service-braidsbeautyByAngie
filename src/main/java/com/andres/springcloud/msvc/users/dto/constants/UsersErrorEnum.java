package com.andres.springcloud.msvc.users.dto.constants;

import com.braidsbeautybyangie.sagapatternspringboot.aggregates.AppExceptions.TypeException;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.util.GenericError;

public enum UsersErrorEnum implements GenericError {
    //General Errors
    PERSON_NOT_FOUND_ERPE00001("ERPE00001", "Person Not Found", "The person with the given ID does not exist.", TypeException.E),
    PERSON_ALREADY_EXISTS_ERPE00002("ERPE00002", "Person Already Exists", "The person with the given email already exists in the system.", TypeException.E),
    PERSON_NOT_FOUND_ERPE00003("ERPE00003", "Person Not Found", "The person with the given ID does not exist in the system.", TypeException.E),
    PERSON_INVALID_DATA_ERPE00004("ERPE00004", "Invalid Person Data", "The provided person data is invalid or incomplete.", TypeException.E),
    // Employee Errors
    EMPLOYEE_NOT_FOUND_ERE00005("ERE00005", "Employee Not Found", "The employee with the given ID does not exist.", TypeException.E),
    EMPLOYEE_ALREADY_EXISTS_ERE00006("ERE00006", "Employee Already Exists", "The employee with the given email already exists in the system.", TypeException.E),
    EMPLOYEE_INVALID_DATA_ERE00007("ERE00007", "Invalid Employee Data", "The provided employee data is invalid or incomplete.", TypeException.E),
    //CUSTOMER ERRORS
    CUSTOMER_NOT_FOUND_ERC00008("ERC00008", "Customer Not Found", "The customer with the given ID does not exist.", TypeException.E),
    CUSTOMER_ALREADY_EXISTS_ERC00009("ERC00009", "Customer Already Exists", "The customer with the given email already exists in the system.", TypeException.E),
    CUSTOMER_INVALID_DATA_ERC00010("ERC00010", "Invalid Customer Data", "The provided customer data is invalid or incomplete.", TypeException.E),
    //Document type
    DOCUMENT_TYPE_ALREADY_EXISTS_ERDT00001("ERDT00001", "Document Type Already Exists", "The document type with the given name already exists.", TypeException.E),
    DOCUMENT_TYPE_NOT_FOUND_ERDT00002("ERDT00002", "Document Type Not Found", "The document type with the given ID does not exist.", TypeException.E),

    //EmployeeType
    EMPLOYEE_TYPE_ALREADY_EXISTS_ERET00001("ERET00001", "Employee Type Already Exists", "The employee type with the given name already exists.", TypeException.E),
    EMPLOYEE_TYPE_NOT_FOUND_ERET00002("ERET00002", "Employee Type Not Found", "The employee type with the given ID does not exist.", TypeException.E),
    //CustomerType
    CUSTOMER_TYPE_ALREADY_EXISTS_ERCT00001("ERCT00001", "Customer Type Already Exists", "The customer type with the given name already exists.", TypeException.E),
    CUSTOMER_TYPE_NOT_FOUND_ERCT00002("ERCT00002", "Customer Type Not Found", "The customer type with the given ID does not exist.", TypeException.E),

    //USERS
    USER_NOT_FOUND_US00001("US00001", "User Not Found", "The user with the given ID does not exist.", TypeException.E),
    USER_ALREADY_EXISTS_US00002("US00002", "User Already Exists", "The user with the given username or email already exists.", TypeException.E),
    USER_INVALID_DATA_US00003("US00003", "Invalid User Data", "The provided user data is invalid or incomplete.", TypeException.E),
    USERNAME_ALREADY_EXISTS_US00004("US00004", "Username Already Exists", "The username is already taken by another user.", TypeException.E),
    USER_EMAIL_ALREADY_EXISTS_US00005("US00005", "Email Already Exists", "The email address is already associated with another user.", TypeException.E),
    USER_PHONE_ALREADY_EXISTS_US00006("US00006", "Phone Already Exists", "The phone number is already associated with another user.", TypeException.E),

    //WARNING
    EMAIL_ALREADY_EXISTS_WAR00011("WAR00011", "Email Already Exists", "The email address is already associated with another user.", TypeException.W),
    PHONE_ALREADY_EXISTS_WAR00012("WAR00012", "Phone Already Exists", "The phone number is already associated with another user.", TypeException.W),
    ;
    private UsersErrorEnum(String code, String title, String message, TypeException type) {
        this.code = code;
        this.title = title;
        this.message = message;
        this.type = type;
    }
    private final String code;
    private final String title;
    private final String message;
    private final TypeException type;


    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public TypeException getType() {
        return type;
    }
}
