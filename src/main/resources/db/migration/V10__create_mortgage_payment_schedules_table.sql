-- Create mortgage_payment_schedules table with composite PK (mortgage_id, installment_number)
CREATE TABLE mortgage_payment_schedules (
    mortgage_id BIGINT NOT NULL,
    installment_number INT NOT NULL,
    due_date DATE,
    principal_due DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    interest_due DECIMAL(19,2) DEFAULT 0.00,
    late_fee DECIMAL(19,2) DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    PRIMARY KEY (mortgage_id, installment_number),
    CONSTRAINT fk_mps_mortgage FOREIGN KEY (mortgage_id) REFERENCES mortgage_accounts(mortgage_id)
);

-- Index for fast lookup by mortgage_id
CREATE INDEX idx_mps_mortgage_id ON mortgage_payment_schedules (mortgage_id);

