-- Create mortgage_account_requests table
CREATE TABLE mortgage_account_requests (
    request_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    id_card_number VARCHAR(20),
    household_registration TEXT,
    marriage_certificate VARCHAR(255),
    labor_contract VARCHAR(255),
    salary_slip VARCHAR(255),
    requested_loan_amount DECIMAL(15,2),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    request_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approval_date TIMESTAMP NULL,
    notes TEXT,
    CONSTRAINT fk_mortgage_request_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

