package com.aitovavi.fleetops.shipment.infrastructure;

import com.aitovavi.fleetops.shipment.domain.Shipment;
import com.aitovavi.fleetops.shipment.domain.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {

    Page<Shipment> findByStatus(
            ShipmentStatus status,
            Pageable pageable
    );

    Page<Shipment> findByCustomerId(
            UUID customerId,
            Pageable pageable
    );

    Page<Shipment> findByStatusAndCustomerId(
            ShipmentStatus status,
            UUID customerId,
            Pageable pageable
    );
}