package org.example.storyreading.ibanking.service;

import org.example.storyreading.ibanking.dto.AccountInfoResponse;
import org.example.storyreading.ibanking.dto.CheckingAccountInfoResponse;

public interface AccountService {

    CheckingAccountInfoResponse getCheckingAccountInfo(Long userId);

    /**
     * Lấy thông tin tài khoản theo account number
     * @param accountNumber Số tài khoản
     * @return AccountInfoResponse chứa thông tin account, user và bank
     */
    AccountInfoResponse getAccountInfoByAccountNumber(String accountNumber);
}
