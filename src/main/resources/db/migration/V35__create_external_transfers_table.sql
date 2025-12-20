-- Migration to create external_transfers table
-- This table stores external bank transfer transactions (transfers to other banks)

CREATE TABLE external_transfers (
    external_transfer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_account_id BIGINT NOT NULL,
    receiver_bank_id BIGINT NOT NULL,
    receiver_account_number VARCHAR(50) NOT NULL,
    receiver_name VARCHAR(100) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    description VARCHAR(255),
    transaction_code VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    otp_code VARCHAR(10),
    otp_expiry TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,

    -- Foreign keys
    CONSTRAINT fk_external_transfer_sender_account
        FOREIGN KEY (sender_account_id) REFERENCES accounts(account_id) ON DELETE CASCADE,
    CONSTRAINT fk_external_transfer_receiver_bank
        FOREIGN KEY (receiver_bank_id) REFERENCES banks(bank_id) ON DELETE CASCADE,

    -- Indexes for performance
    INDEX idx_sender_account_id (sender_account_id),
    INDEX idx_receiver_bank_id (receiver_bank_id),
    INDEX idx_transaction_code (transaction_code),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);

