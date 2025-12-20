package org.example.storyreading.ibanking.service.impl;

import org.example.storyreading.ibanking.dto.AccountInfoResponse;
import org.example.storyreading.ibanking.dto.CheckingAccountInfoResponse;
import org.example.storyreading.ibanking.entity.Account;
import org.example.storyreading.ibanking.entity.Bank;
import org.example.storyreading.ibanking.entity.CheckingAccount;
import org.example.storyreading.ibanking.entity.User;
import org.example.storyreading.ibanking.exception.ResourceNotFoundException;
import org.example.storyreading.ibanking.repository.AccountRepository;
import org.example.storyreading.ibanking.repository.CheckingAccountRepository;
import org.example.storyreading.ibanking.service.AccountService;
import org.example.storyreading.ibanking.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private CheckingAccountRepository checkingAccountRepository;

    @Autowired
    private AccountRepository accountRepository;

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

    @Override
    @Transactional(readOnly = true)
    public AccountInfoResponse getAccountInfoByAccountNumber(String accountNumber) {
        // Tìm account theo account number
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản với số: " + accountNumber));

        // Lấy user info
        User user = account.getUser();
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy thông tin người dùng cho tài khoản: " + accountNumber);
        }

        // Lấy bank info
        Bank bank = user.getBank();
        String bankBin = "";
        String bankCode = "";
        String bankName = "";

        if (bank != null) {
            bankBin = bank.getBankBin();
            bankCode = bank.getBankCode();
            bankName = bank.getBankName();
        }

        //check if bankBIN != "770717
        if (bankBin.equals("770717")){
            throw new RuntimeException("Không tìm thấy thông tin ngân hàng cho tài khoản: " + accountNumber);
        }

        // Tạo response
        AccountInfoResponse response = new AccountInfoResponse();
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountHolderName(user.getFullName());
        response.setBankBin(bankBin);
        response.setBankCode(bankCode);
        response.setBankName(bankName);
        response.setUserId(user.getUserId());
        response.setAccountType(account.getAccountType().name());

        return response;
    }
}
