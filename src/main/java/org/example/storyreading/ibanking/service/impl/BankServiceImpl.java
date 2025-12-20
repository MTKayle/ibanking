package org.example.storyreading.ibanking.service.impl;

import org.example.storyreading.ibanking.dto.BankResponse;
import org.example.storyreading.ibanking.entity.Bank;
import org.example.storyreading.ibanking.exception.ResourceNotFoundException;
import org.example.storyreading.ibanking.repository.BankRepository;
import org.example.storyreading.ibanking.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankServiceImpl implements BankService {

    @Autowired
    private BankRepository bankRepository;

    @Override
    public List<BankResponse> getAllBanks() {
        List<Bank> banks = bankRepository.findAll();
        return banks.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BankResponse getBankById(Long bankId) {
        Bank bank = bankRepository.findById(bankId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ngân hàng với ID: " + bankId));
        return convertToResponse(bank);
    }

    @Override
    public BankResponse getBankByBin(String bankBin) {
        Bank bank = bankRepository.findByBankBin(bankBin)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ngân hàng với BIN: " + bankBin));
        return convertToResponse(bank);
    }

    /**
     * Convert entity sang DTO response
     */
    private BankResponse convertToResponse(Bank bank) {
        BankResponse response = new BankResponse();
        response.setBankId(bank.getBankId());
        response.setBankBin(bank.getBankBin());
        response.setBankCode(bank.getBankCode());
        response.setBankName(bank.getBankName());
        response.setLogoUrl(bank.getLogoUrl());
        return response;
    }
}

