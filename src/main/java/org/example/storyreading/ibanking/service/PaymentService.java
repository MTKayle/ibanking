package org.example.storyreading.ibanking.service;

import org.example.storyreading.ibanking.dto.DepositRequest;
import org.example.storyreading.ibanking.dto.DepositResponse;
import org.example.storyreading.ibanking.dto.WithdrawRequest;
import org.example.storyreading.ibanking.dto.WithdrawResponse;
import org.example.storyreading.ibanking.dto.TransferRequest;
import org.example.storyreading.ibanking.dto.TransferResponse;
import org.example.storyreading.ibanking.dto.OtpResponse;

public interface PaymentService {

    /**
     * Deposit money into a checking account
     * Uses pessimistic lock to ensure transaction safety
     * Automatically records transaction in transactions table
     *
     * @param depositRequest request containing account number and amount
     * @return deposit response with updated balance
     */
    DepositResponse depositToCheckingAccount(DepositRequest depositRequest);

    /**
     * Withdraw money from a checking account (for OFFICER)
     * Uses pessimistic lock to ensure transaction safety
     * Automatically records transaction in transactions table
     *
     * @param withdrawRequest request containing account number and amount
     * @return withdraw response with updated balance
     */
    WithdrawResponse withdrawFromCheckingAccount(WithdrawRequest withdrawRequest);

    /**
     * Transfer money between two checking accounts
     * Uses pessimistic lock (SELECT FOR UPDATE) to prevent race conditions
     * Locks accounts in order by accountId to prevent deadlocks
     * Only allows transfer between checking accounts
     *
     * @param transferRequest request containing sender, receiver account numbers and amount
     * @return transfer response with transaction details and updated balances
     */
    TransferResponse transferMoney(TransferRequest transferRequest);

    /**
     * Khởi tạo giao dịch chuyển tiền và tạo OTP
     * Tạo transaction với trạng thái PENDING
     * Tạo OTP 6 số với thời gian hết hạn 1 phút
     *
     * @param transferRequest request containing sender, receiver account numbers and amount
     * @return OTP response with transaction code and OTP code
     */
    OtpResponse initiateTransferWithOtp(TransferRequest transferRequest);

    /**
     * Xác nhận giao dịch chuyển tiền bằng OTP
     * Verify OTP và thực hiện chuyển tiền
     *
     * @param transactionCode mã giao dịch

     * @return transfer response with transaction details
     */
    TransferResponse confirmTransferWithOtp(String transactionCode);
}
