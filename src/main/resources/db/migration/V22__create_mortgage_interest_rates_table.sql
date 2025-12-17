-- Create mortgage interest rates table
CREATE TABLE IF NOT EXISTS mortgage_interest_rates (
    rate_id BIGSERIAL PRIMARY KEY,
    min_months INTEGER NOT NULL,
    max_months INTEGER,
    interest_rate DECIMAL(10, 4) NOT NULL,
    description VARCHAR(100),
    CONSTRAINT check_months CHECK (min_months > 0),
    CONSTRAINT check_rate CHECK (interest_rate > 0)
);

-- Insert default interest rates based on term
-- Kỳ hạn vay	Lãi suất cố định (%)/năm
-- ≤ 12 tháng	7.5%
INSERT INTO mortgage_interest_rates (min_months, max_months, interest_rate, description)
VALUES (1, 12, 7.5, '≤ 12 tháng');

-- 13 – 24 tháng	8.0%
INSERT INTO mortgage_interest_rates (min_months, max_months, interest_rate, description)
VALUES (13, 24, 8.0, '13 - 24 tháng');

-- 25 – 36 tháng	8.5%
INSERT INTO mortgage_interest_rates (min_months, max_months, interest_rate, description)
VALUES (25, 36, 8.5, '25 - 36 tháng');

-- 37 – 60 tháng	9.0%
INSERT INTO mortgage_interest_rates (min_months, max_months, interest_rate, description)
VALUES (37, 60, 9.0, '37 - 60 tháng');

-- 61 – 120 tháng	9.5%
INSERT INTO mortgage_interest_rates (min_months, max_months, interest_rate, description)
VALUES (61, 120, 9.5, '61 - 120 tháng');

-- > 120 tháng	10.0%
INSERT INTO mortgage_interest_rates (min_months, max_months, interest_rate, description)
VALUES (121, NULL, 10.0, '> 120 tháng');

-- Create index for faster lookup
CREATE INDEX IF NOT EXISTS idx_mortgage_interest_rates_months ON mortgage_interest_rates(min_months, max_months);

