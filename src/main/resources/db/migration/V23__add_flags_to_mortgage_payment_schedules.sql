-- Add is_current_period and is_overdue flags to mortgage_payment_schedules table

ALTER TABLE mortgage_payment_schedules
ADD COLUMN is_current_period BOOLEAN DEFAULT FALSE,
ADD COLUMN is_overdue BOOLEAN DEFAULT FALSE;

-- Add comments for new columns
COMMENT ON COLUMN mortgage_payment_schedules.is_current_period IS 'Đánh dấu kỳ hiện tại cần thanh toán (kỳ chưa trả đầu tiên không quá hạn)';
COMMENT ON COLUMN mortgage_payment_schedules.is_overdue IS 'Đánh dấu kỳ đã quá hạn (chưa trả và đã qua ngày đến hạn)';

