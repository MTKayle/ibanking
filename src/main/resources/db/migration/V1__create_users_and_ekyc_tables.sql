-- Initial migration: Create users table
CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    date_of_birth DATE,
    cccd_number VARCHAR(50) UNIQUE NOT NULL,
    permanent_address VARCHAR(255),
    temporary_address VARCHAR(255),
    role VARCHAR(20) NOT NULL CHECK (role IN ('customer', 'officer')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone);
CREATE INDEX IF NOT EXISTS idx_users_cccd ON users(cccd_number);

-- Create ekyc_photos table
CREATE TABLE IF NOT EXISTS ekyc_photos (
    photo_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    photo_url TEXT NOT NULL,
    image_type VARCHAR(20) NOT NULL CHECK (image_type IN ('cccd', 'selfie')),
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index for ekyc_photos
CREATE INDEX IF NOT EXISTS idx_ekyc_photos_user_id ON ekyc_photos(user_id);

