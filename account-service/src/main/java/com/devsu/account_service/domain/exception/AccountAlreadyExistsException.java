package com.devsu.account_service.domain.exception;

import lombok.Getter;

@Getter
public class AccountAlreadyExistsException extends RuntimeException {
    private final String accountNumber;
    private final String nextSteps;
    
    public AccountAlreadyExistsException(String accountNumber, String message) {
        super(message);
        this.accountNumber = accountNumber;
        this.nextSteps = "Please use a different account number.";
    }
}
