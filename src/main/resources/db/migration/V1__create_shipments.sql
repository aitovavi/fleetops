CREATE TABLE shipments (
    id UUID PRIMARY KEY,
    tracking_number VARCHAR(32) NOT NULL UNIQUE,
    origin_city VARCHAR(120) NOT NULL,
    destination_city VARCHAR(120) NOT NULL,
    cargo_description VARCHAR(500) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT chk_shipments_status CHECK (
        status IN (
            'CREATED',
            'PLANNED',
            'IN_TRANSIT',
            'DELIVERED',
            'CANCELLED'
        )
    ),

    CONSTRAINT chk_shipments_route CHECK (
        origin_city <> destination_city
    )
);

CREATE INDEX idx_shipments_status_created_at
    ON shipments (status, created_at DESC);