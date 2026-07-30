package com.aitovavi.fleetops.customer.infrastructure;

import com.aitovavi.fleetops.customer.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    boolean existsByEmail(String email);
}