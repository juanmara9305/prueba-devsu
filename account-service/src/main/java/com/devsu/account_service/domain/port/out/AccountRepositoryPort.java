package com.devsu.account_service.domain.port.out;

import com.devsu.account_service.domain.model.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountRepositoryPort {
    Mono<Account> save(Account account);
    Flux<Account> findAll();
    Mono<Account> findByAccountNumber(String accountNumber);
    Mono<Boolean> existsByAccountNumber(String accountNumber);
    Flux<Account> findByClientId(String clientId);
}
