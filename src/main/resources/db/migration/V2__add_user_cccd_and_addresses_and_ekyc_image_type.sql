-- Migration: add date_of_birth, cccd_number, permanent_address, temporary_address to users
-- and add image_type to ekyc_photos (cccd | selfie)

-- Add user columns if they don't exist
ALTER TABLE users
  ADD COLUMN IF NOT EXISTS date_of_birth DATE,
  ADD COLUMN IF NOT EXISTS cccd_number VARCHAR(50),
  ADD COLUMN IF NOT EXISTS permanent_address VARCHAR(255),
  ADD COLUMN IF NOT EXISTS temporary_address VARCHAR(255);

-- Create unique index on cccd_number for non-null values (allows multiple NULLs)
CREATE UNIQUE INDEX IF NOT EXISTS ux_users_cccd_number ON users (cccd_number) WHERE cccd_number IS NOT NULL;

-- Add image_type to ekyc_photos with default 'selfie' so existing rows are backfilled
ALTER TABLE ekyc_photos
  ADD COLUMN IF NOT EXISTS image_type VARCHAR(20) DEFAULT 'selfie';

-- Ensure no NULLs remain (defensive)
UPDATE ekyc_photos SET image_type = 'selfie' WHERE image_type IS NULL;

-- Add check constraint for allowed values if not exists
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_ekyc_photos_image_type') THEN
        ALTER TABLE ekyc_photos ADD CONSTRAINT chk_ekyc_photos_image_type CHECK (image_type IN ('cccd','selfie'));
    END IF;
END
$$;

-- Set image_type NOT NULL
ALTER TABLE ekyc_photos
  ALTER COLUMN image_type SET NOT NULL;

-- Optional: you may want to add indexes on date_of_birth or phone if you query them frequently.

