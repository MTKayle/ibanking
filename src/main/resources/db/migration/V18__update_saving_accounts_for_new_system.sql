-- Update saving_accounts table for new saving system
ALTER TABLE saving_accounts
    ADD COLUMN saving_book_number VARCHAR(50) UNIQUE,
    ADD COLUMN term VARCHAR(30) NOT NULL DEFAULT 'NON_TERM',
    MODIFY COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

-- Update existing records
UPDATE saving_accounts SET term = 'NON_TERM' WHERE term IS NULL;
UPDATE saving_accounts SET status = 'ACTIVE' WHERE status IS NULL;

