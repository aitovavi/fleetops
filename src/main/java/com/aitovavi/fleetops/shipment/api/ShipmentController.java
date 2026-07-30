package com.aitovavi.fleetops.shipment.api;

import com.aitovavi.fleetops.shipment.application.ShipmentService;
import com.aitovavi.fleetops.shipment.domain.Shipment;
import com.aitovavi.fleetops.shipment.domain.ShipmentStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @PostMapping
    public ResponseEntity<ShipmentResponse> createShipment(
            @Valid @RequestBody ShipmentRequest request
    ) {
        Shipment shipment = new Shipment();
        shipment.setOriginCity(request.originCity());
        shipment.setDestinationCity(request.destinationCity());
        shipment.setCargoDescription(request.cargoDescription());

        Shipment createdShipment = shipmentService.createShipment(
                shipment,
                request.customerId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ShipmentResponse.from(createdShipment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentResponse> getShipment(
            @PathVariable UUID id
    ) {
        Shipment shipment = shipmentService.getShipment(id);
        return ResponseEntity.ok(ShipmentResponse.from(shipment));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(
            @PathVariable UUID id,
            @RequestParam ShipmentStatus status
    ) {
        shipmentService.changeShipmentStatus(id, status);
        return ResponseEntity.noContent().build();
    }
}