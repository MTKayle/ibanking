package org.example.storyreading.ibanking.service.impl;

import org.example.storyreading.ibanking.dto.CheckingAccountInfoResponse;
import org.example.storyreading.ibanking.entity.Account;
import org.example.storyreading.ibanking.entity.CheckingAccount;
import org.example.storyreading.ibanking.exception.ResourceNotFoundException;
import org.example.storyreading.ibanking.repository.CheckingAccountRepository;
import org.example.storyreading.ibanking.service.AccountService;
import org.example.storyreading.ibanking.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private CheckingAccountRepository checkingAccountRepository;

    @Override
    public CheckingAccountInfoResponse getCheckingAccountInfo(Long userId) {
        CheckingAccount checkingAccount = checkingAccountRepository
                .findCheckingAccountsByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản checking"));

        Account account = checkingAccount.getAccount();

        CheckingAccountInfoResponse resp = new CheckingAccountInfoResponse();
        resp.setAccountNumber(account.getAccountNumber());
        resp.setCheckingId(checkingAccount.getCheckingId());
        resp.setBalance(checkingAccount.getBalance());
        resp.setUserId(account.getUser().getUserId());
        resp.setUserPhone(account.getUser().getPhone());

        return resp;
    }
}
