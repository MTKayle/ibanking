//package org.example.storyreading.ibanking.service;
//
//import org.example.storyreading.ibanking.dto.OtpResponse;
//import org.example.storyreading.ibanking.entity.Transaction;
//import org.example.storyreading.ibanking.entity.TransactionOtp;
//import org.example.storyreading.ibanking.exception.ResourceNotFoundException;
//import org.example.storyreading.ibanking.repository.TransactionOtpRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.Instant;
//import java.time.temporal.ChronoUnit;
//import java.util.Random;
//
//@Service
//public class TransactionOtpService {
//
//    @Autowired
//    private TransactionOtpRepository otpRepository;
//
//    private final Random random = new Random();
//
//    /**
//     * Tạo OTP 6 số unique
//     */
//    private String generateUniqueOtp() {
//        // Tạo OTP 6 số ngẫu nhiên
//        return String.format("%06d", random.nextInt(1000000));
//    }
//
//    /**
//     * Tạo OTP cho transaction
//     */
//    @Transactional
//    public OtpResponse createOtpForTransaction(Transaction transaction) {
//        // Kiểm tra xem transaction đã có OTP chưa
//        if (otpRepository.existsByTransaction_TransactionId(transaction.getTransactionId())) {
//            throw new IllegalStateException("OTP already exists for this transaction");
//        }
//
//        // Tạo OTP
//        String otpCode = generateUniqueOtp();
//
//        // Tính thời gian hết hạn (1 phút từ bây giờ)
//        Instant expiresAt = Instant.now().plus(1, ChronoUnit.MINUTES);
//
//        // Lưu OTP vào database
//        TransactionOtp otp = new TransactionOtp();
//        otp.setTransaction(transaction);
//        otp.setOtpCode(otpCode);
//        otp.setExpiresAt(expiresAt);
//        otp.setIsVerified(false);
//        otp.setAttempts(0);
//
//        TransactionOtp savedOtp = otpRepository.save(otp);
//
//        // Tạo response
//        return new OtpResponse(
//                transaction.getCode(),
//                otpCode,
//                expiresAt.toString(),
//                "OTP đã được tạo thành công. OTP sẽ hết hạn sau 1 phút."
//        );
//    }
//
//    /**
//     * Xác thực OTP
//     */
//    @Transactional
//    public boolean verifyOtp(String transactionCode, String otpCode) {
//        // Tìm OTP theo transaction code
//        TransactionOtp otp = otpRepository.findByTransactionCode(transactionCode)
//                .orElseThrow(() -> new ResourceNotFoundException("OTP not found for transaction: " + transactionCode));
//
//        // Tăng số lần thử
//        otp.incrementAttempts();
//        otpRepository.save(otp);
//
//        // Kiểm tra số lần thử (tối đa 3 lần)
//        if (otp.getAttempts() > 3) {
//            throw new IllegalStateException("OTP verification failed: Maximum attempts exceeded");
//        }
//
//        // Kiểm tra OTP đã được verify chưa
//        if (otp.getIsVerified()) {
//            throw new IllegalStateException("OTP has already been verified");
//        }
//
//        // Kiểm tra OTP đã hết hạn chưa
//        if (otp.isExpired()) {
//            throw new IllegalStateException("OTP has expired");
//        }
//
//        // Kiểm tra OTP có đúng không
//        if (!otp.getOtpCode().equals(otpCode)) {
//            throw new IllegalStateException("Invalid OTP code");
//        }
//
//        // Đánh dấu OTP đã được verify
//        otp.setIsVerified(true);
//        otp.setVerifiedAt(Instant.now());
//        otpRepository.save(otp);
//
//        return true;
//    }
//
//    /**
//     * Lấy thông tin OTP theo transaction code
//     */
//    @Transactional(readOnly = true)
//    public TransactionOtp getOtpByTransactionCode(String transactionCode) {
//        return otpRepository.findByTransactionCode(transactionCode)
//                .orElseThrow(() -> new ResourceNotFoundException("OTP not found for transaction: " + transactionCode));
//    }
//
//    /**
//     * Kiểm tra OTP có hết hạn không
//     */
//    @Transactional(readOnly = true)
//    public boolean isOtpExpired(String transactionCode) {
//        TransactionOtp otp = getOtpByTransactionCode(transactionCode);
//        return otp.isExpired();
//    }
//}
//
