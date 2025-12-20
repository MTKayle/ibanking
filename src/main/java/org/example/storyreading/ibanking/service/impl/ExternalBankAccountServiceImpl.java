package org.example.storyreading.ibanking.service.impl;

import org.example.storyreading.ibanking.dto.ExternalAccountInfoResponse;
import org.example.storyreading.ibanking.entity.ExternalBankAccount;
import org.example.storyreading.ibanking.exception.ResourceNotFoundException;
import org.example.storyreading.ibanking.repository.ExternalBankAccountRepository;
import org.example.storyreading.ibanking.service.ExternalBankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExternalBankAccountServiceImpl implements ExternalBankAccountService {

    @Autowired
    private ExternalBankAccountRepository externalBankAccountRepository;

    @Override
    public ExternalAccountInfoResponse getAccountInfoByBankBinAndAccountNumber(String bankBin, String accountNumber) {
        // Tìm tài khoản theo bankBin và accountNumber
        ExternalBankAccount account = externalBankAccountRepository
                .findByBankBinAndAccountNumber(bankBin, accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy tài khoản với số tài khoản " + accountNumber)
                );

        // Convert sang DTO response
        return convertToResponse(account);
    }

    /**
     * Convert entity sang DTO response
     */
    private ExternalAccountInfoResponse convertToResponse(ExternalBankAccount account) {
        ExternalAccountInfoResponse response = new ExternalAccountInfoResponse();
        response.setExternalAccountId(account.getExternalAccountId());
        response.setFullName(account.getFullName());
        response.setAccountNumber(account.getAccountNumber());
        response.setBankId(account.getBank().getBankId());
        response.setBankName(account.getBank().getBankName());
        response.setBankCode(account.getBank().getBankCode());
        response.setBankBin(account.getBank().getBankBin());
        return response;
    }
}

