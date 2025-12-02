package org.example.storyreading.ibanking.service;

import org.example.storyreading.ibanking.dto.DepositRequest;
import org.example.storyreading.ibanking.dto.DepositResponse;

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

}

