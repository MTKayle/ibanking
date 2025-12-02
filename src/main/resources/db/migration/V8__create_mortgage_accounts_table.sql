-- Create mortgage_accounts table with shared PK to accounts (mortgage_id -> accounts.account_id)
CREATE TABLE mortgage_accounts (
    mortgage_id BIGINT PRIMARY KEY,
    principal_amount DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    interest_rate DECIMAL(10,4) DEFAULT 0.0000,
    monthly_principal_payment_amount DECIMAL(19,2) DEFAULT 0.00,
    term_months INT,
    start_date DATE,
    CONSTRAINT fk_mortgage_account_account FOREIGN KEY (mortgage_id) REFERENCES accounts(account_id)
);

-- Optionally add checks if DB supports it
-- ALTER TABLE mortgage_accounts ADD CONSTRAINT chk_term_months_positive CHECK (term_months IS NULL OR term_months >= 0);

