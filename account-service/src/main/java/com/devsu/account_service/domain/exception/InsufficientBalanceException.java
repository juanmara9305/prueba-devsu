package com.devsu.account_service.domain.exception;

import lombok.Getter;

@Getter
public class InsufficientBalanceException extends RuntimeException {
    private final String nextSteps;
    
    public InsufficientBalanceException(String message) {
        super(message);
        this.nextSteps = "Please deposit funds or reduce the withdrawal amount.";
    }
}
