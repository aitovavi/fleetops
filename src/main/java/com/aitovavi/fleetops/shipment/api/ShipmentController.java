package com.aitovavi.fleetops.shipment.api;

import com.aitovavi.fleetops.shipment.application.ShipmentService;
import com.aitovavi.fleetops.shipment.domain.Shipment;
import com.aitovavi.fleetops.shipment.domain.ShipmentStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.aitovavi.fleetops.shipment.api.ShipmentRequest;
import com.aitovavi.fleetops.customer.domain.Customer;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @PostMapping
    public ResponseEntity<Shipment> createShipment(@RequestBody ShipmentRequest request) {
        Shipment shipment = new Shipment();
        shipment.setOriginCity(request.getOriginCity());
        shipment.setDestinationCity(request.getDestinationCity());
        shipment.setCargoDescription(request.getCargoDescription());

        Shipment created = shipmentService.createShipment(shipment, request.getCustomerId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shipment> getShipment(@PathVariable UUID id) {
        Shipment shipment = shipmentService.getShipment(id);
        return ResponseEntity.ok(shipment);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(
            @PathVariable UUID id,
            @RequestParam ShipmentStatus status) {
        shipmentService.changeShipmentStatus(id, status);
        return ResponseEntity.noContent().build();
    }
}