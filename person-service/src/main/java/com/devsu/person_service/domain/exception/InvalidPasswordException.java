package com.devsu.person_service.domain.exception;

import lombok.Getter;

@Getter
public class InvalidPasswordException extends RuntimeException {
    private final String nextSteps;
    
    public InvalidPasswordException(String message) {
        super(message);
        this.nextSteps = "Password requirements: minimum 8 characters, at least 1 lowercase letter, 1 uppercase letter, and 1 digit.";
    }
}
