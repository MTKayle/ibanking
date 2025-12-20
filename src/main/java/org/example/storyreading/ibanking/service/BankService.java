package org.example.storyreading.ibanking.service;

import org.example.storyreading.ibanking.dto.BankResponse;

import java.util.List;

public interface BankService {

    /**
     * Lấy danh sách tất cả ngân hàng
     */
    List<BankResponse> getAllBanks();

    /**
     * Lấy thông tin ngân hàng theo ID
     */
    BankResponse getBankById(Long bankId);

    /**
     * Lấy thông tin ngân hàng theo BIN
     */
    BankResponse getBankByBin(String bankBin);
}

