package org.example.storyreading.ibanking.service;

import org.example.storyreading.ibanking.dto.DepositRequest;
import org.example.storyreading.ibanking.dto.DepositResponse;
import org.example.storyreading.ibanking.dto.TransferRequest;
import org.example.storyreading.ibanking.dto.TransferResponse;

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
     * Transfer money between two checking accounts
     * Uses pessimistic lock (SELECT FOR UPDATE) to prevent race conditions
     * Locks accounts in order by accountId to prevent deadlocks
     * Only allows transfer between checking accounts
     *
     * @param transferRequest request containing sender, receiver account numbers and amount
     * @return transfer response with transaction details and updated balances
     */
    TransferResponse transferMoney(TransferRequest transferRequest);
}
