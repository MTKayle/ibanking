package org.example.storyreading.ibanking.service.impl;

import org.example.storyreading.ibanking.entity.Transaction;
import org.example.storyreading.ibanking.repository.TransactionRepository;
import org.example.storyreading.ibanking.service.TransactionFailureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionFailureServiceImpl implements TransactionFailureService {

    @Autowired
    private TransactionRepository transactionRepository;


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(String transactionCode, Exception e) {
        // Tìm transaction theo code trong transaction mới
        Transaction transaction = transactionRepository.findByCode(transactionCode)
                .orElse(null);

        if (transaction != null) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transaction.setDescription(
                    (transaction.getDescription() != null ? transaction.getDescription() + " - " : "") +
                            "Lỗi: " + e.getMessage()
            );
            transactionRepository.save(transaction);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markSuccess(String transactionCode) {
        // Tìm transaction theo code trong transaction mới
        Transaction transaction = transactionRepository.findByCode(transactionCode)
                .orElse(null);

        if (transaction != null) {
            transaction.setStatus(Transaction.TransactionStatus.SUCCESS);
            transactionRepository.save(transaction);
        }
    }

}
