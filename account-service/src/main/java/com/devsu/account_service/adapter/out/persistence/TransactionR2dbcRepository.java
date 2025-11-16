package com.devsu.account_service.adapter.out.persistence;

import com.devsu.account_service.adapter.out.persistence.entity.TransactionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@Repository
public interface TransactionR2dbcRepository extends ReactiveCrudRepository<TransactionEntity, Long> {
    Flux<TransactionEntity> findByAccountNumber(String accountNumber);
    Flux<TransactionEntity> findByAccountNumberAndDateBetween(
        String accountNumber, 
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
}
