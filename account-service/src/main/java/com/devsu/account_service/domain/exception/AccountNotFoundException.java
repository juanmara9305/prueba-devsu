package com.devsu.account_service.domain.exception;

import lombok.Getter;

@Getter
public class AccountNotFoundException extends RuntimeException {
    private final String accountNumber;
    private final String nextSteps;
    
    public AccountNotFoundException(String accountNumber, String message) {
        super(message);
        this.accountNumber = accountNumber;
        this.nextSteps = "Please verify the account number is correct and the account exists.";
    }
}
