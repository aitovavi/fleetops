package com.aitovavi.fleetops.customer.application;

import com.aitovavi.fleetops.common.api.error.ConflictException;
import com.aitovavi.fleetops.common.api.error.ResourceNotFoundException;
import com.aitovavi.fleetops.customer.domain.Customer;
import com.aitovavi.fleetops.customer.infrastructure.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService(customerRepository);
    }

    @Test
    void shouldCreateCustomerWithUniqueEmail() {
        Customer customer = createCustomer(
                "Ivan Petrov",
                "ivan@example.com"
        );

        when(customerRepository.existsByEmail(customer.getEmail()))
                .thenReturn(false);

        when(customerRepository.save(customer))
                .thenReturn(customer);

        Customer result = customerService.createCustomer(customer);

        assertSame(customer, result);

        verify(customerRepository)
                .existsByEmail("ivan@example.com");

        verify(customerRepository).save(customer);
    }

    @Test
    void shouldRejectDuplicateEmail() {
        Customer customer = createCustomer(
                "Ivan Petrov",
                "ivan@example.com"
        );

        when(customerRepository.existsByEmail(customer.getEmail()))
                .thenReturn(true);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> customerService.createCustomer(customer)
        );

        assertEquals(
                "Customer with email already exists: ivan@example.com",
                exception.getMessage()
        );

        verify(customerRepository)
                .existsByEmail("ivan@example.com");

        verify(customerRepository, never()).save(any());
    }

    @Test
    void shouldReturnAllCustomers() {
        Customer firstCustomer = createCustomer(
                "Ivan Petrov",
                "ivan@example.com"
        );

        Customer secondCustomer = createCustomer(
                "Anna Smirnova",
                "anna@example.com"
        );

        when(customerRepository.findAll())
                .thenReturn(List.of(firstCustomer, secondCustomer));

        List<Customer> result = customerService.getAllCustomers();

        assertEquals(2, result.size());
        assertSame(firstCustomer, result.get(0));
        assertSame(secondCustomer, result.get(1));

        verify(customerRepository).findAll();
    }

    @Test
    void shouldReturnCustomerById() {
        UUID customerId = UUID.randomUUID();

        Customer customer = createCustomer(
                "Ivan Petrov",
                "ivan@example.com"
        );
        customer.setId(customerId);

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        Customer result = customerService.getCustomerById(customerId);

        assertSame(customer, result);
        verify(customerRepository).findById(customerId);
    }

    @Test
    void shouldThrowNotFoundWhenCustomerDoesNotExist() {
        UUID customerId = UUID.randomUUID();

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.getCustomerById(customerId)
        );

        assertEquals(
                "Customer not found with id: " + customerId,
                exception.getMessage()
        );

        verify(customerRepository).findById(customerId);
    }

    private Customer createCustomer(
            String name,
            String email
    ) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhoneNumber("+79990000000");
        customer.setCompanyName("Logistics Test");
        return customer;
    }
}