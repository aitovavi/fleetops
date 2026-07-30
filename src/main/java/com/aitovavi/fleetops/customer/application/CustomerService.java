package com.aitovavi.fleetops.customer.application;

import com.aitovavi.fleetops.common.api.error.ConflictException;
import com.aitovavi.fleetops.common.api.error.ResourceNotFoundException;
import com.aitovavi.fleetops.customer.domain.Customer;
import com.aitovavi.fleetops.customer.infrastructure.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Customer createCustomer(Customer customer) {
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new ConflictException(
                    "Customer with email already exists: " + customer.getEmail()
            );
        }

        return customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Customer getCustomerById(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + id
                ));
    }
}