package com.aitovavi.fleetops.shipment.api;

import com.aitovavi.fleetops.customer.api.CustomerResponse;
import com.aitovavi.fleetops.shipment.domain.Shipment;
import com.aitovavi.fleetops.shipment.domain.ShipmentStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ShipmentResponse(
        UUID id,
        String trackingNumber,
        String originCity,
        String destinationCity,
        String cargoDescription,
        ShipmentStatus status,
        long version,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        CustomerResponse customer
) {

    public static ShipmentResponse from(Shipment shipment) {
        CustomerResponse customerResponse = shipment.getCustomer() == null
                ? null
                : CustomerResponse.from(shipment.getCustomer());

        return new ShipmentResponse(
                shipment.getId(),
                shipment.getTrackingNumber(),
                shipment.getOriginCity(),
                shipment.getDestinationCity(),
                shipment.getCargoDescription(),
                shipment.getStatus(),
                shipment.getVersion(),
                shipment.getCreatedAt(),
                shipment.getUpdatedAt(),
                customerResponse
        );
    }
}