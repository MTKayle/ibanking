-- Create banks table
CREATE TABLE banks (
    bank_id BIGSERIAL PRIMARY KEY,
    bank_bin VARCHAR(10) NOT NULL UNIQUE,
    bank_code VARCHAR(20) NOT NULL UNIQUE,
    bank_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Insert common Vietnamese banks
INSERT INTO banks (bank_bin, bank_code, bank_name, created_at) VALUES
                                                                   ('970405', 'AGRIBANK', 'Ngan hang Nong nghiep va Phat trien Nong thon Viet Nam', NOW()),
                                                                   ('970422', 'MBBANK', 'Ngan hang Quan doi', NOW()),
                                                                   ('970407', 'TECHCOMBANK', 'Ngan hang Ky thuong Viet Nam', NOW()),
                                                                   ('970415', 'VIETINBANK', 'Ngan hang Cong thuong Viet Nam', NOW()),
                                                                   ('970436', 'VIETCOMBANK', 'Ngan hang Ngoai thuong Viet Nam', NOW()),
                                                                   ('970418', 'BIDV', 'Ngan hang Dau tu va Phat trien Viet Nam', NOW()),
                                                                   ('970403', 'SACOMBANK', 'Ngan hang Sai Gon Thuong tin', NOW()),
                                                                   ('970416', 'ACB', 'Ngan hang A Chau', NOW()),
                                                                   ('970432', 'VPBANK', 'Ngan hang Viet Nam Thinh Vuong', NOW()),
                                                                   ('970423', 'TPBANK', 'Ngan hang Tien Phong', NOW()),
                                                                   ('970441', 'VIB', 'Ngan hang Quoc te', NOW()),
                                                                   ('970448', 'OCB', 'Ngan hang Phuong Dong', NOW()),
                                                                   ('970426', 'MSB', 'Ngan hang Hang hai', NOW()),
                                                                   ('970414', 'OCEANBANK', 'Ngan hang Dai Duong', NOW()),
                                                                   ('970409', 'BAB', 'Ngan hang Bac A', NOW()),
                                                                   ('970438', 'BAOVIETBANK', 'Ngan hang Bao Viet', NOW()),
                                                                   ('970433', 'VIETBANK', 'Ngan hang Viet Nam Thuong tin', NOW()),
                                                                   ('970434', 'INDOVINABANK', 'Ngan hang Indovina', NOW()),
                                                                   ('770717', 'HATBANK', 'Ngan hang thuong mai HAT', NOW());


COMMENT ON TABLE banks IS 'Danh sach cac ngan hang Viet Nam';
COMMENT ON COLUMN banks.bank_bin IS 'Ma BIN cua ngan hang (Bank Identification Number)';
COMMENT ON COLUMN banks.bank_code IS 'Ma ngan hang (viet tat)';
COMMENT ON COLUMN banks.bank_name IS 'Ten day du cua ngan hang';

