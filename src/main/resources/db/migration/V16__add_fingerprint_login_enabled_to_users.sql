-- Add fingerprint_login_enabled column to users table
ALTER TABLE users
ADD COLUMN fingerprint_login_enabled BOOLEAN NOT NULL DEFAULT FALSE;

