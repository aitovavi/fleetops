package com.aitovavi.fleetops.shipment.application;

import com.aitovavi.fleetops.customer.domain.Customer;
import com.aitovavi.fleetops.customer.infrastructure.CustomerRepository;
import com.aitovavi.fleetops.shipment.domain.Shipment;
import com.aitovavi.fleetops.shipment.domain.ShipmentStatus;
import com.aitovavi.fleetops.shipment.infrastructure.ShipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final CustomerRepository customerRepository;

    public ShipmentService(ShipmentRepository shipmentRepository, CustomerRepository customerRepository) {
        this.shipmentRepository = shipmentRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Shipment createShipment(Shipment shipment, UUID customerId) {
        // Проверка городов
        if (shipment.getOriginCity().equalsIgnoreCase(shipment.getDestinationCity())) {
            throw new IllegalArgumentException("Origin city and destination city must be different");
        }

        // Загрузка клиента, если передан customerId
        if (customerId != null) {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + customerId));
            shipment.setCustomer(customer);
        }

        return shipmentRepository.save(shipment);
    }

    @Transactional(readOnly = true)
    public Shipment getShipment(UUID id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found with id: " + id));
    }

    @Transactional
    public void changeShipmentStatus(UUID id, ShipmentStatus newStatus) {
        Shipment shipment = getShipment(id);
        shipment.changeStatus(newStatus);
        shipmentRepository.save(shipment);
    }
}