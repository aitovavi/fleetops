package com.aitovavi.fleetops.shipment.application;

import com.aitovavi.fleetops.shipment.domain.Shipment;
import com.aitovavi.fleetops.shipment.domain.ShipmentStatus;
import com.aitovavi.fleetops.shipment.infrastructure.ShipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;

    public ShipmentService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    @Transactional
    public Shipment createShipment(Shipment shipment) {
        // Проверяем, что город отправления и назначения не совпадают
        if (shipment.getOriginCity().equalsIgnoreCase(shipment.getDestinationCity())) {
            throw new IllegalArgumentException("Origin city and destination city must be different");
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