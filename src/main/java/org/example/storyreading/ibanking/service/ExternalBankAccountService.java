package org.example.storyreading.ibanking.service;

import org.example.storyreading.ibanking.dto.ExternalAccountInfoResponse;

public interface ExternalBankAccountService {

    /**
     * Lấy thông tin tài khoản ngân hàng ngoài theo bankBin và accountNumber
     */
    ExternalAccountInfoResponse getAccountInfoByBankBinAndAccountNumber(String bankBin, String accountNumber);
}

