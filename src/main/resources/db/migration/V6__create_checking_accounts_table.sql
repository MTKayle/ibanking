-- Create checking_accounts table with shared PK to accounts
CREATE TABLE checking_accounts (
    account_id BIGINT PRIMARY KEY,
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    overdraft_limit DECIMAL(19,2) DEFAULT 0.00,
    CONSTRAINT fk_checking_account_account FOREIGN KEY (account_id) REFERENCES accounts(account_id)
);

