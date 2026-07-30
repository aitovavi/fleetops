package com.aitovavi.fleetops.customer.api;

import com.aitovavi.fleetops.customer.application.CustomerService;
import com.aitovavi.fleetops.customer.domain.Customer;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(
            @Valid @RequestBody CustomerCreateRequest request
    ) {
        Customer customer = new Customer();
        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setPhoneNumber(request.phoneNumber());
        customer.setCompanyName(request.companyName());

        Customer createdCustomer = customerService.createCustomer(customer);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CustomerResponse.from(createdCustomer));
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        List<CustomerResponse> customers = customerService
                .getAllCustomers()
                .stream()
                .map(CustomerResponse::from)
                .toList();

        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(
            @PathVariable UUID id
    ) {
        Customer customer = customerService.getCustomerById(id);

        return ResponseEntity.ok(CustomerResponse.from(customer));
    }
}