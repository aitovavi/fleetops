package com.aitovavi.fleetops.shipment.application;

import com.aitovavi.fleetops.common.api.error.ResourceNotFoundException;
import com.aitovavi.fleetops.customer.domain.Customer;
import com.aitovavi.fleetops.customer.infrastructure.CustomerRepository;
import com.aitovavi.fleetops.shipment.domain.Shipment;
import com.aitovavi.fleetops.shipment.domain.ShipmentStatus;
import com.aitovavi.fleetops.shipment.infrastructure.ShipmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final CustomerRepository customerRepository;

    public ShipmentService(
            ShipmentRepository shipmentRepository,
            CustomerRepository customerRepository
    ) {
        this.shipmentRepository = shipmentRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Shipment createShipment(Shipment shipment, UUID customerId) {
        if (shipment.getOriginCity().equalsIgnoreCase(
                shipment.getDestinationCity()
        )) {
            throw new IllegalArgumentException(
                    "Origin city and destination city must be different"
            );
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + customerId
                ));

        shipment.setCustomer(customer);

        return shipmentRepository.save(shipment);
    }

    @Transactional(readOnly = true)
    public Shipment getShipment(UUID id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Shipment not found with id: " + id
                ));
    }

    @Transactional(readOnly = true)
    public Page<Shipment> getShipments(
            ShipmentStatus status,
            UUID customerId,
            Pageable pageable
    ) {
        if (status != null && customerId != null) {
            return shipmentRepository.findByStatusAndCustomerId(
                    status,
                    customerId,
                    pageable
            );
        }

        if (status != null) {
            return shipmentRepository.findByStatus(status, pageable);
        }

        if (customerId != null) {
            return shipmentRepository.findByCustomerId(customerId, pageable);
        }

        return shipmentRepository.findAll(pageable);
    }

    @Transactional
    public void changeShipmentStatus(
            UUID id,
            ShipmentStatus newStatus
    ) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Shipment not found with id: " + id
                ));

        shipment.changeStatus(newStatus);
    }
}