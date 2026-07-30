package com.aitovavi.fleetops.customer.api;

import com.aitovavi.fleetops.customer.domain.Customer;

import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String name,
        String email,
        String phoneNumber,
        String companyName,
        boolean active
) {

    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getCompanyName(),
                customer.isActive()
        );
    }
}