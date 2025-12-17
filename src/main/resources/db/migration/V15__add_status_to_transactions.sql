-- Add status column to transactions table
ALTER TABLE transactions
ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'SUCCESS';

-- Add comment to explain the column
COMMENT ON COLUMN transactions.status IS 'Transaction status: PENDING, SUCCESS, FAILED';

