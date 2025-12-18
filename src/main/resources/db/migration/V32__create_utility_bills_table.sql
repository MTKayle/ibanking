-- Create utility_bills table for electricity, water, internet, phone bills

CREATE TABLE utility_bills (
    bill_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bill_code VARCHAR(20) NOT NULL UNIQUE,
    bill_type VARCHAR(20) NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    customer_address VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(15),
    period VARCHAR(7) NOT NULL,
    usage_amount INT,
    old_index INT,
    new_index INT,
    unit_price DECIMAL(19, 2),
    amount DECIMAL(19, 2) NOT NULL,
    vat DECIMAL(19, 2),
    total_amount DECIMAL(19, 2) NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
    payment_time DATETIME,
    paid_by_user_id BIGINT,
    transaction_id VARCHAR(255),
    provider_name VARCHAR(100),
    provider_code VARCHAR(20),
    notes VARCHAR(500),
    version BIGINT DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_utility_bill_user FOREIGN KEY (paid_by_user_id) REFERENCES users(user_id),
    INDEX idx_bill_code (bill_code),
    INDEX idx_bill_status (status),
    INDEX idx_bill_due_date (due_date),
    INDEX idx_paid_by_user (paid_by_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

