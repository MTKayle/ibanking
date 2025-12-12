-- Add bank_id foreign key to users table
ALTER TABLE users ADD COLUMN bank_id BIGINT;
ALTER TABLE users ADD CONSTRAINT fk_users_bank FOREIGN KEY (bank_id) REFERENCES banks(bank_id);

COMMENT ON COLUMN users.bank_id IS 'Ngan hang cua user (moi user chi co 1 ngan hang)';

