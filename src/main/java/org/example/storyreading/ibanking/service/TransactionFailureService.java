package org.example.storyreading.ibanking.service;

import org.example.storyreading.ibanking.entity.Transaction;

public interface TransactionFailureService {
    void markFailed(String transactionCode, Exception e);
    void markSuccess(String transactionCode);
}
