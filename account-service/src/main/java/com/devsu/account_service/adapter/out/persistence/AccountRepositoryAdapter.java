package com.devsu.account_service.adapter.out.persistence;

import com.devsu.account_service.adapter.out.persistence.entity.AccountEntity;
import com.devsu.account_service.adapter.out.persistence.mapper.AccountPersistenceMapper;
import com.devsu.account_service.domain.model.Account;
import com.devsu.account_service.domain.port.out.AccountRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRepositoryPort {
    private final AccountR2dbcRepository repository;
    private final AccountPersistenceMapper mapper;
    
    @Override
    public Mono<Account> save(Account account) {
        AccountEntity entity = mapper.toEntity(account);
        if (entity.getId() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        
        return repository.save(entity)
            .map(mapper::toDomain);
    }
    
    @Override
    public Flux<Account> findAll() {
        return repository.findAll()
            .map(mapper::toDomain);
    }
    
    @Override
    public Mono<Account> findByAccountNumber(String accountNumber) {
        return repository.findByAccountNumber(accountNumber)
            .map(mapper::toDomain);
    }
    
    @Override
    public Mono<Boolean> existsByAccountNumber(String accountNumber) {
        return repository.existsByAccountNumber(accountNumber);
    }
    
    @Override
    public Flux<Account> findByClientId(String clientId) {
        return repository.findByClientId(clientId)
            .map(mapper::toDomain);
    }
}
