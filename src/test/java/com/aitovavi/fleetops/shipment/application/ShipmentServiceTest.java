package com.aitovavi.fleetops.shipment.application;

import com.aitovavi.fleetops.common.api.error.ResourceNotFoundException;
import com.aitovavi.fleetops.customer.domain.Customer;
import com.aitovavi.fleetops.customer.infrastructure.CustomerRepository;
import com.aitovavi.fleetops.shipment.domain.Shipment;
import com.aitovavi.fleetops.shipment.domain.ShipmentStatus;
import com.aitovavi.fleetops.shipment.infrastructure.ShipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private CustomerRepository customerRepository;

    private ShipmentService shipmentService;

    @BeforeEach
    void setUp() {
        shipmentService = new ShipmentService(
                shipmentRepository,
                customerRepository
        );
    }

    @Test
    void shouldCreateShipmentForExistingCustomer() {
        UUID customerId = UUID.randomUUID();

        Customer customer = new Customer();
        customer.setId(customerId);

        Shipment shipment = createShipment("Samara", "Kazan");

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        when(shipmentRepository.save(shipment))
                .thenReturn(shipment);

        Shipment result = shipmentService.createShipment(
                shipment,
                customerId
        );

        assertSame(shipment, result);
        assertSame(customer, result.getCustomer());

        verify(customerRepository).findById(customerId);
        verify(shipmentRepository).save(shipment);
    }

    @Test
    void shouldRejectShipmentWithEqualCities() {
        UUID customerId = UUID.randomUUID();
        Shipment shipment = createShipment("Samara", "samara");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> shipmentService.createShipment(shipment, customerId)
        );

        assertEquals(
                "Origin city and destination city must be different",
                exception.getMessage()
        );

        verifyNoInteractions(
                customerRepository,
                shipmentRepository
        );
    }

    @Test
    void shouldThrowNotFoundWhenCustomerDoesNotExist() {
        UUID customerId = UUID.randomUUID();
        Shipment shipment = createShipment("Samara", "Kazan");

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> shipmentService.createShipment(shipment, customerId)
        );

        assertEquals(
                "Customer not found with id: " + customerId,
                exception.getMessage()
        );

        verify(customerRepository).findById(customerId);
        verifyNoInteractions(shipmentRepository);
    }

    @Test
    void shouldReturnExistingShipment() {
        UUID shipmentId = UUID.randomUUID();
        Shipment shipment = createShipment("Samara", "Kazan");
        shipment.setId(shipmentId);

        when(shipmentRepository.findById(shipmentId))
                .thenReturn(Optional.of(shipment));

        Shipment result = shipmentService.getShipment(shipmentId);

        assertSame(shipment, result);
        verify(shipmentRepository).findById(shipmentId);
    }

    @Test
    void shouldThrowNotFoundWhenShipmentDoesNotExist() {
        UUID shipmentId = UUID.randomUUID();

        when(shipmentRepository.findById(shipmentId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> shipmentService.getShipment(shipmentId)
        );

        assertEquals(
                "Shipment not found with id: " + shipmentId,
                exception.getMessage()
        );
    }

    @Test
    void shouldChangeShipmentStatus() {
        UUID shipmentId = UUID.randomUUID();
        Shipment shipment = createShipment("Samara", "Kazan");
        shipment.setId(shipmentId);

        when(shipmentRepository.findById(shipmentId))
                .thenReturn(Optional.of(shipment));

        shipmentService.changeShipmentStatus(
                shipmentId,
                ShipmentStatus.PLANNED
        );

        assertEquals(
                ShipmentStatus.PLANNED,
                shipment.getStatus()
        );

        verify(shipmentRepository).findById(shipmentId);
        verify(shipmentRepository, never()).save(any());
    }

    private Shipment createShipment(
            String originCity,
            String destinationCity
    ) {
        Shipment shipment = new Shipment();
        shipment.setOriginCity(originCity);
        shipment.setDestinationCity(destinationCity);
        shipment.setCargoDescription("Computer equipment");
        return shipment;
    }
}