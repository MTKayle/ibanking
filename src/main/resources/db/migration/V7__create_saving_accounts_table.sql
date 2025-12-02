-- Create saving_accounts table with shared PK to accounts using saving_id
CREATE TABLE saving_accounts (
    saving_id VARCHAR(50) PRIMARY KEY,
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    type VARCHAR(20) NOT NULL DEFAULT 'DEMAND',
    interest_rate DECIMAL(10,4) DEFAULT 0.0000,
    opened_date DATE,
    maturity_date DATE,
    profit_per_month DECIMAL(19,2) DEFAULT 0.00,
    next_profit_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_saving_account_account FOREIGN KEY (saving_id) REFERENCES accounts(account_number)
);

-- Optionally add checks for type and status if your DB supports CHECK constraints
-- ALTER TABLE saving_accounts ADD CONSTRAINT chk_saving_type CHECK (type IN ('FIXED_TERM','DEMAND'));
-- ALTER TABLE saving_accounts ADD CONSTRAINT chk_saving_status CHECK (status IN ('ACTIVE','CLOSED','LOCKED'));
