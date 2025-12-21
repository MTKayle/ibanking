-- Thêm column fcm_token vào bảng users để lưu Firebase Cloud Messaging token
-- Mỗi user chỉ có 1 device token tại 1 thời điểm
ALTER TABLE users ADD COLUMN IF NOT EXISTS fcm_token TEXT;

-- Tạo index để tìm kiếm nhanh theo fcm_token (nếu cần)
CREATE INDEX IF NOT EXISTS idx_users_fcm_token ON users(fcm_token);

