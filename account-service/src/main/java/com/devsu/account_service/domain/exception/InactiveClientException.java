package com.devsu.account_service.domain.exception;

import lombok.Getter;

@Getter
public class InactiveClientException extends RuntimeException {
    private final String clientId;
    private final String nextSteps;
    
    public InactiveClientException(String clientId, String message) {
        super(message);
        this.clientId = clientId;
        this.nextSteps = "Please activate the client before creating an account.";
    }
}
