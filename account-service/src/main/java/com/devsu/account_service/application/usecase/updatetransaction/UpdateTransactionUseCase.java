package com.devsu.account_service.application.usecase.updatetransaction;

import com.devsu.account_service.domain.exception.InsufficientBalanceException;
import com.devsu.account_service.domain.exception.TransactionNotFoundException;
import com.devsu.account_service.domain.model.Transaction;
import com.devsu.account_service.domain.port.in.UpdateTransactionPort;
import com.devsu.account_service.domain.port.out.AccountRepositoryPort;
import com.devsu.account_service.domain.port.out.TransactionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class UpdateTransactionUseCase implements UpdateTransactionPort {
    private final TransactionRepositoryPort transactionRepositoryPort;
    private final AccountRepositoryPort accountRepositoryPort;
    
    @Override
    public Mono<Transaction> execute(UpdateTransactionCommand command) {
        return transactionRepositoryPort.findById(command.getId())
            .switchIfEmpty(Mono.error(new TransactionNotFoundException(
                command.getId(),
                "Transaction not found")))
            .flatMap(existingTransaction -> {
                BigDecimal oldAmount = existingTransaction.getAmount();
                BigDecimal newAmount = command.getAmount();
                BigDecimal difference = newAmount.subtract(oldAmount);
                
                return accountRepositoryPort.findByAccountNumber(existingTransaction.getAccountNumber())
                    .flatMap(account -> {
                        BigDecimal newBalance = account.getBalance().add(difference);
                        
                        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                            return Mono.error(new InsufficientBalanceException(
                                "Saldo no disponible"));
                        }
                        
                        account.setBalance(newBalance);
                        existingTransaction.setAmount(newAmount);
                        existingTransaction.setBalance(newBalance);
                        existingTransaction.setTransactionType(command.getTransactionType());
                        
                        return accountRepositoryPort.save(account)
                            .then(transactionRepositoryPort.save(existingTransaction));
                    });
            });
    }
}
