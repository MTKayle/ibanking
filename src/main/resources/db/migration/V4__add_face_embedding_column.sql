-- Add face_embedding column to users table
-- This column stores the face_token from Face++ API for face recognition login

ALTER TABLE users
ADD COLUMN face_embedding TEXT NULL;

-- Add comment to describe the column
COMMENT ON COLUMN users.face_embedding IS 'Face token from Face++ API used for face recognition authentication';

