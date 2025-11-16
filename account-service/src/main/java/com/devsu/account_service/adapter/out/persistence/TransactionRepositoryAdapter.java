package com.devsu.account_service.adapter.out.persistence;

import com.devsu.account_service.adapter.out.persistence.entity.TransactionEntity;
import com.devsu.account_service.adapter.out.persistence.mapper.TransactionPersistenceMapper;
import com.devsu.account_service.domain.model.Transaction;
import com.devsu.account_service.domain.port.out.TransactionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {
    private final TransactionR2dbcRepository repository;
    private final TransactionPersistenceMapper mapper;
    
    @Override
    public Mono<Transaction> save(Transaction transaction) {
        if (transaction.getId() != null) {
            return repository.findById(transaction.getId())
                .flatMap(existingEntity -> {
                    TransactionEntity entity = mapper.toEntity(transaction);
                    entity.setCreatedAt(existingEntity.getCreatedAt());
                    entity.setUpdatedAt(LocalDateTime.now());
                    return repository.save(entity);
                })
                .map(mapper::toDomain);
        } else {
            TransactionEntity entity = mapper.toEntity(transaction);
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
            return repository.save(entity)
                .map(mapper::toDomain);
        }
    }
    
    @Override
    public Flux<Transaction> findAll() {
        return repository.findAll()
            .map(mapper::toDomain);
    }
    
    @Override
    public Mono<Transaction> findById(Long id) {
        return repository.findById(id)
            .map(mapper::toDomain);
    }
    
    @Override
    public Flux<Transaction> findByAccountNumber(String accountNumber) {
        return repository.findByAccountNumber(accountNumber)
            .map(mapper::toDomain);
    }
    
    @Override
    public Flux<Transaction> findByAccountNumberAndDateBetween(
            String accountNumber, 
            LocalDateTime startDate, 
            LocalDateTime endDate) {
        return repository.findByAccountNumberAndDateBetween(accountNumber, startDate, endDate)
            .map(mapper::toDomain);
    }
}
