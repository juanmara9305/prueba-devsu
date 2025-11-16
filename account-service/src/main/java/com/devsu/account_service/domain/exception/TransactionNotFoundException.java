package com.devsu.account_service.domain.exception;

import lombok.Getter;

@Getter
public class TransactionNotFoundException extends RuntimeException {
    private final Long transactionId;
    private final String nextSteps;
    
    public TransactionNotFoundException(Long transactionId, String message) {
        super(message);
        this.transactionId = transactionId;
        this.nextSteps = "Please verify the transaction ID is correct.";
    }
}
