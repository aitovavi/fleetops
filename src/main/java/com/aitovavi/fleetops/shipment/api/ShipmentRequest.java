package com.aitovavi.fleetops.shipment.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ShipmentRequest(

        @NotBlank(message = "Origin city is required")
        @Size(max = 120, message = "Origin city must not exceed 120 characters")
        String originCity,

        @NotBlank(message = "Destination city is required")
        @Size(max = 120, message = "Destination city must not exceed 120 characters")
        String destinationCity,

        @NotBlank(message = "Cargo description is required")
        @Size(max = 500, message = "Cargo description must not exceed 500 characters")
        String cargoDescription,

        @NotNull(message = "Customer ID is required")
        UUID customerId
) {
}