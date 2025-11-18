package com.devsu.person_service.domain.exception;

import lombok.Getter;

@Getter
public class ClientAlreadyExistsException extends RuntimeException {
    private final String identification;
    private final String nextSteps;
    
    public ClientAlreadyExistsException(String identification, String message) {
        super(message);
        this.identification = identification;
        this.nextSteps = "Please use a different identification or retrieve the existing client.";
    }
}
