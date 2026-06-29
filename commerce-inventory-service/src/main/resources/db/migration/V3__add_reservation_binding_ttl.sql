ALTER TABLE inv_reservations
    ADD COLUMN IF NOT EXISTS bound_order BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX IF NOT EXISTS idx_inv_reservations_unbound_expiry
    ON inv_reservations (bound_order, available_to)
    WHERE cancelled_time IS NULL AND remaining > 0;
