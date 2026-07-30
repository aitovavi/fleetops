package com.aitovavi.fleetops.shipment.api;

import com.aitovavi.fleetops.common.api.PageResponse;
import com.aitovavi.fleetops.shipment.application.ShipmentService;
import com.aitovavi.fleetops.shipment.domain.Shipment;
import com.aitovavi.fleetops.shipment.domain.ShipmentStatus;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping
    public ResponseEntity<PageResponse<ShipmentResponse>> getShipments(
            @RequestParam(required = false) ShipmentStatus status,
            @RequestParam(required = false) UUID customerId,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        Page<ShipmentResponse> shipments = shipmentService
                .getShipments(status, customerId, pageable)
                .map(ShipmentResponse::from);

        return ResponseEntity.ok(PageResponse.from(shipments));
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