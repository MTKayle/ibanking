package org.example.storyreading.ibanking.service;

import org.example.storyreading.ibanking.dto.ExternalTransferRequest;
import org.example.storyreading.ibanking.dto.ExternalTransferResponse;
import org.example.storyreading.ibanking.dto.OtpResponse;

public interface ExternalTransferService {

    /**
     * Khởi tạo giao dịch chuyển tiền ngoài ngân hàng và tạo OTP
     * - Validate sender account và receiver bank
     * - Tạo ExternalTransfer với trạng thái PENDING
     * - Tạo OTP 6 số với thời gian hết hạn 1 phút
     * - Chưa trừ tiền ở bước này
     *
     * @param request thông tin chuyển tiền ngoài
     * @return OTP response với transaction code và OTP code
     */
    OtpResponse initiateExternalTransfer(ExternalTransferRequest request);

    /**
     * Xác nhận giao dịch chuyển tiền ngoài ngân hàng bằng OTP
     * - Verify OTP và transaction code
     * - Trừ tiền từ tài khoản sender (không cộng tiền cho receiver vì là ngoài hệ thống)
     * - Update status thành SUCCESS hoặc FAILED
     *
     * @param transactionCode mã giao dịch

     * @return response với thông tin giao dịch đã hoàn thành
     */
    ExternalTransferResponse confirmExternalTransfer(String transactionCode);
}

