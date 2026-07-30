package com.aitovavi.fleetops.shipment.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShipmentTest {

    @Test
    void shouldMoveThroughCompleteDeliveryLifecycle() {
        Shipment shipment = new Shipment();

        shipment.changeStatus(ShipmentStatus.PLANNED);
        assertEquals(ShipmentStatus.PLANNED, shipment.getStatus());

        shipment.changeStatus(ShipmentStatus.IN_TRANSIT);
        assertEquals(ShipmentStatus.IN_TRANSIT, shipment.getStatus());

        shipment.changeStatus(ShipmentStatus.DELIVERED);
        assertEquals(ShipmentStatus.DELIVERED, shipment.getStatus());
    }

    @Test
    void shouldCancelCreatedShipment() {
        Shipment shipment = new Shipment();

        shipment.changeStatus(ShipmentStatus.CANCELLED);

        assertEquals(ShipmentStatus.CANCELLED, shipment.getStatus());
    }

    @Test
    void shouldRejectInvalidTransitionFromCreatedToDelivered() {
        Shipment shipment = new Shipment();

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> shipment.changeStatus(ShipmentStatus.DELIVERED)
        );

        assertEquals(
                "Shipment status cannot be changed from CREATED to DELIVERED",
                exception.getMessage()
        );

        assertEquals(ShipmentStatus.CREATED, shipment.getStatus());
    }

    @Test
    void shouldRejectTransitionFromDeliveredStatus() {
        Shipment shipment = new Shipment();

        shipment.changeStatus(ShipmentStatus.PLANNED);
        shipment.changeStatus(ShipmentStatus.IN_TRANSIT);
        shipment.changeStatus(ShipmentStatus.DELIVERED);

        assertThrows(
                IllegalStateException.class,
                () -> shipment.changeStatus(ShipmentStatus.IN_TRANSIT)
        );

        assertEquals(ShipmentStatus.DELIVERED, shipment.getStatus());
    }

    @Test
    void shouldDoNothingWhenStatusDoesNotChange() {
        Shipment shipment = new Shipment();

        assertDoesNotThrow(
                () -> shipment.changeStatus(ShipmentStatus.CREATED)
        );

        assertEquals(ShipmentStatus.CREATED, shipment.getStatus());
    }
}