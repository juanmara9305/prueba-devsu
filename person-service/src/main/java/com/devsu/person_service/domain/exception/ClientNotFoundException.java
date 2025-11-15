package com.devsu.person_service.domain.exception;

import lombok.Getter;

@Getter
public class ClientNotFoundException extends RuntimeException {
    private final String clientId;
    private final String nextSteps;
    
    public ClientNotFoundException(String clientId, String message) {
        super(message);
        this.clientId = clientId;
        this.nextSteps = "Please verify the client ID is correct and the client exists in the system.";
    }
}
