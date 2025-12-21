-- Migration để tạo bảng bank_branches
-- Lưu thông tin chi nhánh ngân hàng với tọa độ địa lý

CREATE TABLE bank_branches (
    branch_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    address VARCHAR(255),
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tạo index cho việc tìm kiếm theo tọa độ
CREATE INDEX idx_bank_branches_location ON bank_branches(latitude, longitude);

    -- Thêm một số dữ liệu mẫu (tọa độ một số chi nhánh tại TP. Hồ Chí Minh)
    INSERT INTO bank_branches (name, address, latitude, longitude, created_at) VALUES
    ('HAT Chi nhánh Quận 1 TP.HCM', 'Số 235 Nguyễn Văn Cừ, Quận 1, TP. Hồ Chí Minh', 10.7626, 106.6826, CURRENT_TIMESTAMP),
    ('HAT Chi nhánh Quận 3 TP.HCM', 'Số 123 Nam Kỳ Khởi Nghĩa, Quận 3, TP. Hồ Chí Minh', 10.7770, 106.6898, CURRENT_TIMESTAMP),
    ('HAT Chi nhánh Bình Thạnh TP.HCM', 'Số 45 Điện Biên Phủ, Bình Thạnh, TP. Hồ Chí Minh', 10.8022, 106.7147, CURRENT_TIMESTAMP),
    ('HAT Chi nhánh Tân Bình TP.HCM', 'Số 88 Hoàng Văn Thụ, Tân Bình, TP. Hồ Chí Minh', 10.7994, 106.6545, CURRENT_TIMESTAMP),
    ('HAT Chi nhánh Phú Nhuận TP.HCM', 'Số 210 Phan Xích Long, Phú Nhuận, TP. Hồ Chí Minh', 10.7991, 106.6835, CURRENT_TIMESTAMP),
    ('HAT Chi nhánh Quận 7 TP.HCM', 'Số 1028 Nguyễn Văn Linh, Quận 7, TP. Hồ Chí Minh', 10.7332, 106.7196, CURRENT_TIMESTAMP),
    ('HAT Chi nhánh Gò Vấp TP.HCM', 'Số 56 Phan Văn Trị, Gò Vấp, TP. Hồ Chí Minh', 10.8410, 106.6665, CURRENT_TIMESTAMP),
    ('HAT Chi nhánh Thủ Đức TP.HCM', 'Số 216 Võ Văn Ngân, Thủ Đức, TP. Hồ Chí Minh', 10.8509, 106.7717, CURRENT_TIMESTAMP);
