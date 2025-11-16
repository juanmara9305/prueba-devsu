package com.devsu.account_service.domain.port.out;

import com.devsu.account_service.domain.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface TransactionRepositoryPort {
    Mono<Transaction> save(Transaction transaction);
    Flux<Transaction> findAll();
    Mono<Transaction> findById(Long id);
    Flux<Transaction> findByAccountNumber(String accountNumber);
    Flux<Transaction> findByAccountNumberAndDateBetween(
        String accountNumber, 
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
}
