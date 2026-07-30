ALTER TABLE shipments
    ADD COLUMN customer_id UUID;

ALTER TABLE shipments
    ADD CONSTRAINT fk_shipments_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers (id)
        ON DELETE SET NULL;

CREATE INDEX idx_shipments_customer_id
    ON shipments (customer_id);