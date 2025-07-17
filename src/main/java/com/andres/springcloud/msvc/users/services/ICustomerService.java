package com.andres.springcloud.msvc.users.services;

import com.andres.springcloud.msvc.users.dto.CustomerDto;
import com.andres.springcloud.msvc.users.dto.request.CreateCustomerRequest;
import com.andres.springcloud.msvc.users.dto.request.ResponseListPageableCustomer;

import java.util.List;

public interface ICustomerService {

    CustomerDto createCustomer(CreateCustomerRequest createCustomerRequest);
    CustomerDto getCustomerById(Long customerId);
    CustomerDto updateCustomer(Long customerId, CreateCustomerRequest updateCustomerRequest);
    void deleteCustomer(Long customerId);
    CustomerDto getCustomerByEmail(String email);
    ResponseListPageableCustomer getAllCustomersPageable(int pageNumber, int pageSize, String orderBy, String sortDir);

}
