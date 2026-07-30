package com.aitovavi.fleetops.shipment.domain;

import com.aitovavi.fleetops.customer.domain.Customer;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tracking_number", nullable = false, unique = true, length = 32)
    private String trackingNumber;

    @Column(name = "origin_city", nullable = false, length = 120)
    private String originCity;

    @Column(name = "destination_city", nullable = false, length = 120)
    private String destinationCity;

    @Column(name = "cargo_description", nullable = false, length = 500)
    private String cargoDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ShipmentStatus status = ShipmentStatus.CREATED;

    @Version
    @Column(nullable = false)
    private long version;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        if (trackingNumber == null) {
            trackingNumber = "FOP-"
                    + UUID.randomUUID()
                    .toString()
                    .substring(0, 12)
                    .toUpperCase();
        }

        if (createdAt == null) {
            createdAt = now;
        }

        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public void changeStatus(ShipmentStatus targetStatus) {
        if (targetStatus == status) {
            return;
        }

        Set<ShipmentStatus> allowedTargets = switch (status) {
            case CREATED -> EnumSet.of(
                    ShipmentStatus.PLANNED,
                    ShipmentStatus.CANCELLED
            );
            case PLANNED -> EnumSet.of(
                    ShipmentStatus.IN_TRANSIT,
                    ShipmentStatus.CANCELLED
            );
            case IN_TRANSIT -> EnumSet.of(
                    ShipmentStatus.DELIVERED
            );
            case DELIVERED, CANCELLED ->
                    EnumSet.noneOf(ShipmentStatus.class);
        };

        if (!allowedTargets.contains(targetStatus)) {
            throw new IllegalStateException(
                    "Shipment status cannot be changed from "
                            + status
                            + " to "
                            + targetStatus
            );
        }

        status = targetStatus;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public String getCargoDescription() {
        return cargoDescription;
    }

    public void setCargoDescription(String cargoDescription) {
        this.cargoDescription = cargoDescription;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}