package org.example.storyreading.ibanking.service.impl;

import org.example.storyreading.ibanking.entity.Account;
import org.example.storyreading.ibanking.entity.Transaction;
import org.example.storyreading.ibanking.repository.TransactionRepository;
import org.example.storyreading.ibanking.service.TransactionCreationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransactionCreationServiceImpl implements TransactionCreationService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Transaction createPendingTransaction(
            Account senderAccount,
            Account receiverAccount,
            BigDecimal amount,
            Transaction.TransactionType type,
            String description,
            String code
    ) {
        Transaction transaction = new Transaction();
        transaction.setSenderAccount(senderAccount);
        transaction.setReceiverAccount(receiverAccount);
        transaction.setAmount(amount);
        transaction.setTransactionType(type);
        transaction.setDescription(description);
        transaction.setCode(code);
        transaction.setStatus(Transaction.TransactionStatus.PENDING);

        return transactionRepository.save(transaction);
    }


}

