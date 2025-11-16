package com.devsu.account_service.application.usecase.getalltransactions;

import com.devsu.account_service.domain.model.Transaction;
import com.devsu.account_service.domain.port.in.GetAllTransactionsPort;
import com.devsu.account_service.domain.port.out.TransactionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class GetAllTransactionsUseCase implements GetAllTransactionsPort {
    private final TransactionRepositoryPort transactionRepositoryPort;
    
    @Override
    public Flux<Transaction> execute(Void input) {
        return transactionRepositoryPort.findAll();
    }
}
