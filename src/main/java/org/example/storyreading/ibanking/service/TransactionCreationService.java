package org.example.storyreading.ibanking.service;

import org.example.storyreading.ibanking.entity.Account;
import org.example.storyreading.ibanking.entity.Transaction;

import java.math.BigDecimal;

public interface TransactionCreationService {
    Transaction createPendingTransaction(
            Account senderAccount,
            Account receiverAccount,
            BigDecimal amount,
            Transaction.TransactionType type,
            String description,
            String code
    );

}

