-- Update mortgage_accounts table structure
ALTER TABLE mortgage_accounts
    ADD COLUMN IF NOT EXISTS status VARCHAR(30) NOT NULL DEFAULT 'PENDING_APPRAISAL',
    ADD COLUMN IF NOT EXISTS collateral_type VARCHAR(100),
    ADD COLUMN IF NOT EXISTS collateral_description TEXT,
    ADD COLUMN IF NOT EXISTS cccd_front_url TEXT,
    ADD COLUMN IF NOT EXISTS cccd_back_url TEXT,
    ADD COLUMN IF NOT EXISTS collateral_document_urls TEXT,
    ADD COLUMN IF NOT EXISTS payment_frequency VARCHAR(20) NOT NULL DEFAULT 'MONTHLY',
    ADD COLUMN IF NOT EXISTS rejection_reason TEXT,
    ADD COLUMN IF NOT EXISTS created_date DATE,
    ADD COLUMN IF NOT EXISTS approval_date DATE;

-- Remove old column if exists
ALTER TABLE mortgage_accounts
    DROP COLUMN IF EXISTS monthly_principal_payment_amount;

