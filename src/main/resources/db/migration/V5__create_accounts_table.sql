-- Create accounts table
CREATE TABLE accounts (
    account_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    account_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    account_number VARCHAR(50) NOT NULL,
    CONSTRAINT uk_accounts_account_number UNIQUE (account_number),
    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Optionally, restrict account_type and status values using CHECK (if supported by DB)
-- For MySQL older versions, CHECK is parsed but ignored; if using a DB that supports CHECK, you can add:
-- ALTER TABLE accounts ADD CONSTRAINT chk_account_type CHECK (account_type IN ('checking','saving','mortgage'));
-- ALTER TABLE accounts ADD CONSTRAINT chk_status CHECK (status IN ('active','closed'));

