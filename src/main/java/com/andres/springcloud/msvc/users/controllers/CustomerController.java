package com.andres.springcloud.msvc.users.controllers;

import com.andres.springcloud.msvc.users.dto.request.CreateCustomerRequest;
import com.andres.springcloud.msvc.users.services.ICustomerService;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.Constants;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user-service/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final ICustomerService customerService;

    @GetMapping("/list/pageable")
    public ResponseEntity<ApiResponse> listCustomerPageableList(@RequestParam(value = "pageNo", defaultValue = Constants.NUM_PAG_BY_DEFECT, required = false) int pageNo,
                                                                @RequestParam(value = "pageSize", defaultValue = Constants.SIZE_PAG_BY_DEFECT, required = false) int pageSize,
                                                                @RequestParam(value = "sortBy", defaultValue = Constants.ORDER_BY_DEFECT_ALL, required = false) String sortBy,
                                                                @RequestParam(value = "sortDir", defaultValue = Constants.ORDER_DIRECT_BY_DEFECT, required = false) String sortDir) {
        return ResponseEntity.ok(ApiResponse.ok("List of customers retrieved successfully",
                customerService.getAllCustomersPageable(pageNo, pageSize, sortBy, sortDir)));
    }

    @GetMapping("/findById/{customerId}")
    public ResponseEntity<ApiResponse> findCustomerById(@PathVariable(name = "customerId") Long customerId) {
        return ResponseEntity.ok(ApiResponse.ok("Customer retrieved successfully",
                customerService.getCustomerById(customerId)));
    }

    @GetMapping("/findByEmail/{email}")
    public ResponseEntity<ApiResponse> findCustomerByEmail(@PathVariable(name = "email") String email) {
        return ResponseEntity.ok(ApiResponse.ok("Customer retrieved successfully",
                customerService.getCustomerByEmail(email)));
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponse> saveCustomer(@RequestBody CreateCustomerRequest requestCustomer) {
        return ResponseEntity.ok(ApiResponse.create("Customer saved successfully",
                customerService.createCustomer(requestCustomer)));
    }

    @PutMapping("/update/{customerId}")
    public ResponseEntity<ApiResponse> updateCustomer(@PathVariable(name = "customerId") Long customerId,
                                                      @RequestBody CreateCustomerRequest requestCustomer) {
        return ResponseEntity.ok(ApiResponse.create("Customer updated successfully",
                customerService.updateCustomer(customerId, requestCustomer)));
    }

    @DeleteMapping("/delete/{customerId}")
    public ResponseEntity<ApiResponse> deleteCustomer(@PathVariable(name = "customerId") Long customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.ok(ApiResponse.ok("Customer deleted successfully", true));
    }
}