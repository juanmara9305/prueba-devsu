package com.devsu.person_service.domain.exception;

import lombok.Getter;

@Getter
public class ClientAlreadyExistsException extends RuntimeException {
    private final String clientId;
    private final String nextSteps;
    
    public ClientAlreadyExistsException(String clientId, String message) {
        super(message);
        this.clientId = clientId;
        this.nextSteps = "Please use a different client ID or retrieve the existing client.";
    }
}
