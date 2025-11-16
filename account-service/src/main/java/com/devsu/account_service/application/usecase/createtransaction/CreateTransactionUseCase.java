package com.devsu.account_service.application.usecase.createtransaction;

import com.devsu.account_service.domain.exception.AccountNotFoundException;
import com.devsu.account_service.domain.exception.InsufficientBalanceException;
import com.devsu.account_service.domain.model.Transaction;
import com.devsu.account_service.domain.port.in.CreateTransactionPort;
import com.devsu.account_service.domain.port.out.AccountRepositoryPort;
import com.devsu.account_service.domain.port.out.TransactionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CreateTransactionUseCase implements CreateTransactionPort {
    private final AccountRepositoryPort accountRepositoryPort;
    private final TransactionRepositoryPort transactionRepositoryPort;
    
    @Override
    public Mono<Transaction> execute(CreateTransactionCommand command) {
        return accountRepositoryPort.findByAccountNumber(command.getAccountNumber())
            .switchIfEmpty(Mono.error(new AccountNotFoundException(
                command.getAccountNumber(),
                "Account not found")))
            .flatMap(account -> {
                BigDecimal newBalance = account.getBalance().add(command.getAmount());
                
                if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                    return Mono.error(new InsufficientBalanceException(
                        "Saldo no disponible"));
                }
                
                account.setBalance(newBalance);
                
                Transaction transaction = new Transaction();
                transaction.setDate(LocalDateTime.now());
                transaction.setTransactionType(command.getTransactionType());
                transaction.setAmount(command.getAmount());
                transaction.setBalance(newBalance);
                transaction.setAccountNumber(command.getAccountNumber());
                
                return accountRepositoryPort.save(account)
                    .then(transactionRepositoryPort.save(transaction));
            });
    }
}
