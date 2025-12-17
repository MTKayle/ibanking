-- Create mortgage_payment_schedules table
CREATE TABLE IF NOT EXISTS mortgage_payment_schedules (
    schedule_id BIGSERIAL PRIMARY KEY,
    mortgage_id BIGINT NOT NULL,
    period_number INTEGER NOT NULL,
    due_date DATE NOT NULL,
    principal_amount DECIMAL(19, 2) NOT NULL DEFAULT 0,
    interest_amount DECIMAL(19, 2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(19, 2) NOT NULL DEFAULT 0,
    penalty_amount DECIMAL(19, 2) DEFAULT 0,
    remaining_balance DECIMAL(19, 2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    paid_date DATE,
    paid_amount DECIMAL(19, 2) DEFAULT 0,
    overdue_days INTEGER DEFAULT 0,
    CONSTRAINT fk_mortgage_payment_schedule_mortgage
        FOREIGN KEY (mortgage_id) REFERENCES mortgage_accounts(mortgage_id)
        ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_mortgage_payment_schedule_mortgage_id ON mortgage_payment_schedules(mortgage_id);
CREATE INDEX IF NOT EXISTS idx_mortgage_payment_schedule_status ON mortgage_payment_schedules(status);
CREATE INDEX IF NOT EXISTS idx_mortgage_payment_schedule_due_date ON mortgage_payment_schedules(due_date);

