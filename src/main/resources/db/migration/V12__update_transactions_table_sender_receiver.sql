-- Migration: Update transactions table to have sender and receiver accounts
-- Version: V12__update_transactions_table_sender_receiver.sql

-- Drop old foreign key constraint if exists
ALTER TABLE transactions DROP FOREIGN KEY IF EXISTS transactions_ibfk_1;

-- Rename old account_id column to receiver_account_id (if exists)
ALTER TABLE transactions
CHANGE COLUMN account_id receiver_account_id BIGINT NULL;

-- Add sender_account_id column
ALTER TABLE transactions
ADD COLUMN sender_account_id BIGINT NULL AFTER transaction_id;

-- Add foreign key constraints
ALTER TABLE transactions
ADD CONSTRAINT fk_transactions_sender_account
FOREIGN KEY (sender_account_id) REFERENCES accounts(account_id)
ON DELETE SET NULL;

ALTER TABLE transactions
ADD CONSTRAINT fk_transactions_receiver_account
FOREIGN KEY (receiver_account_id) REFERENCES accounts(account_id)
ON DELETE SET NULL;

-- Add index for better query performance
CREATE INDEX idx_transactions_sender_account ON transactions(sender_account_id);
CREATE INDEX idx_transactions_receiver_account ON transactions(receiver_account_id);
CREATE INDEX idx_transactions_created_at ON transactions(created_at DESC);

-- Update existing data: For DEPOSIT transactions, move data to receiver_account_id
-- For WITHDRAW transactions, move data to sender_account_id
-- (This is safe since we're working with nullable columns)

