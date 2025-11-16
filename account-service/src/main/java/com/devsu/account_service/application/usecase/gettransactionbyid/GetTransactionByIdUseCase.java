package com.devsu.account_service.application.usecase.gettransactionbyid;

import com.devsu.account_service.domain.exception.TransactionNotFoundException;
import com.devsu.account_service.domain.model.Transaction;
import com.devsu.account_service.domain.port.in.GetTransactionByIdPort;
import com.devsu.account_service.domain.port.out.TransactionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GetTransactionByIdUseCase implements GetTransactionByIdPort {
    private final TransactionRepositoryPort transactionRepositoryPort;
    
    @Override
    public Mono<Transaction> execute(Long id) {
        return transactionRepositoryPort.findById(id)
            .switchIfEmpty(Mono.error(new TransactionNotFoundException(
                id,
                "Transaction not found")));
    }
}
