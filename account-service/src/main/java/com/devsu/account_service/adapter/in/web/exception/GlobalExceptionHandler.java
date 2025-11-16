package com.devsu.account_service.adapter.in.web.exception;

import com.devsu.account_service.adapter.in.web.dto.ErrorResponse;
import com.devsu.account_service.domain.exception.AccountAlreadyExistsException;
import com.devsu.account_service.domain.exception.AccountNotFoundException;
import com.devsu.account_service.domain.exception.ClientNotFoundException;
import com.devsu.account_service.domain.exception.InactiveClientException;
import com.devsu.account_service.domain.exception.InsufficientBalanceException;
import com.devsu.account_service.domain.exception.TransactionNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(AccountNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleAccountNotFound(
            AccountNotFoundException ex,
            ServerWebExchange exchange) {
        log.error("Account not found: {}", ex.getAccountNumber(), ex);
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Account Not Found")
            .message(ex.getMessage())
            .nextSteps(ex.getNextSteps())
            .path(exchange.getRequest().getPath().value())
            .build();
            
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
    }
    
    @ExceptionHandler(AccountAlreadyExistsException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleAccountConflict(
            AccountAlreadyExistsException ex,
            ServerWebExchange exchange) {
        log.error("Account already exists: {}", ex.getAccountNumber(), ex);
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.CONFLICT.value())
            .error("Account Already Exists")
            .message(ex.getMessage())
            .nextSteps(ex.getNextSteps())
            .path(exchange.getRequest().getPath().value())
            .build();
            
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(error));
    }
    
    @ExceptionHandler(TransactionNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleTransactionNotFound(
            TransactionNotFoundException ex,
            ServerWebExchange exchange) {
        log.error("Transaction not found: {}", ex.getTransactionId(), ex);
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Transaction Not Found")
            .message(ex.getMessage())
            .nextSteps(ex.getNextSteps())
            .path(exchange.getRequest().getPath().value())
            .build();
            
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
    }
    
    @ExceptionHandler(InsufficientBalanceException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInsufficientBalance(
            InsufficientBalanceException ex,
            ServerWebExchange exchange) {
        log.error("Insufficient balance", ex);
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Insufficient Balance")
            .message(ex.getMessage())
            .nextSteps(ex.getNextSteps())
            .path(exchange.getRequest().getPath().value())
            .build();
            
        return Mono.just(ResponseEntity.badRequest().body(error));
    }
    
    @ExceptionHandler(ClientNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleClientNotFound(
            ClientNotFoundException ex,
            ServerWebExchange exchange) {
        log.error("Client not found: {}", ex.getClientId(), ex);
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Client Not Found")
            .message(ex.getMessage())
            .nextSteps(ex.getNextSteps())
            .path(exchange.getRequest().getPath().value())
            .build();
            
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
    }
    
    @ExceptionHandler(InactiveClientException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInactiveClient(
            InactiveClientException ex,
            ServerWebExchange exchange) {
        log.error("Inactive client: {}", ex.getClientId(), ex);
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Inactive Client")
            .message(ex.getMessage())
            .nextSteps(ex.getNextSteps())
            .path(exchange.getRequest().getPath().value())
            .build();
            
        return Mono.just(ResponseEntity.badRequest().body(error));
    }
    
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidation(
            WebExchangeBindException ex,
            ServerWebExchange exchange) {
        log.error("Validation error", ex);
        
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Failed")
            .message(message)
            .nextSteps("Please correct the validation errors and try again.")
            .path(exchange.getRequest().getPath().value())
            .build();
            
        return Mono.just(ResponseEntity.badRequest().body(error));
    }
    
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneral(
            Exception ex,
            ServerWebExchange exchange) {
        log.error("Unexpected error", ex);
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Internal Server Error")
            .message("An unexpected error occurred. Please try again later.")
            .nextSteps("If the problem persists, please contact support.")
            .path(exchange.getRequest().getPath().value())
            .build();
            
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
    }
}
