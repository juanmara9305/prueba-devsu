package com.devsu.account_service.application.usecase.updatetransaction;

import com.devsu.account_service.domain.exception.InsufficientBalanceException;
import com.devsu.account_service.domain.exception.TransactionNotFoundException;
import com.devsu.account_service.domain.model.Account;
import com.devsu.account_service.domain.model.AccountType;
import com.devsu.account_service.domain.model.Transaction;
import com.devsu.account_service.domain.port.out.AccountRepositoryPort;
import com.devsu.account_service.domain.port.out.TransactionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateTransactionUseCaseTest {

    @Mock
    private TransactionRepositoryPort transactionRepositoryPort;

    @Mock
    private AccountRepositoryPort accountRepositoryPort;

    @InjectMocks
    private UpdateTransactionUseCase updateTransactionUseCase;

    private Account account;
    private Transaction existingTransaction;

    @BeforeEach
    void setUp() {
        account = new Account(
            1L,
            "1234567890",
            AccountType.AHORROS,
            new BigDecimal("1000.00"),
            true,
            "client-1",
            "John Doe",
            true
        );

        existingTransaction = new Transaction(
            1L,
            LocalDateTime.now(),
            "Deposit",
            new BigDecimal("500.00"),
            new BigDecimal("1500.00"),
            "1234567890"
        );
    }

    @Test
    void execute_shouldUpdateTransaction_whenValidData() {
        UpdateTransactionCommand command = new UpdateTransactionCommand(
            1L,
            "Deposit",
            new BigDecimal("600.00")
        );

        Transaction updatedTransaction = new Transaction(
            1L,
            existingTransaction.getDate(),
            "Deposit",
            new BigDecimal("600.00"),
            new BigDecimal("1100.00"),
            "1234567890"
        );

        when(transactionRepositoryPort.findById(anyLong())).thenReturn(Mono.just(existingTransaction));
        when(accountRepositoryPort.findByAccountNumber(anyString())).thenReturn(Mono.just(account));
        when(accountRepositoryPort.save(any(Account.class))).thenReturn(Mono.just(account));
        when(transactionRepositoryPort.save(any(Transaction.class))).thenReturn(Mono.just(updatedTransaction));

        StepVerifier.create(updateTransactionUseCase.execute(command))
            .expectNextMatches(t -> 
                t.getAmount().compareTo(new BigDecimal("600.00")) == 0
            )
            .verifyComplete();
    }

    @Test
    void execute_shouldRecalculateBalance_whenTransactionAmountChanges() {
        UpdateTransactionCommand command = new UpdateTransactionCommand(
            1L,
            "Deposit",
            new BigDecimal("300.00")
        );

        Transaction updatedTransaction = new Transaction(
            1L,
            existingTransaction.getDate(),
            "Deposit",
            new BigDecimal("300.00"),
            new BigDecimal("800.00"),
            "1234567890"
        );

        when(transactionRepositoryPort.findById(anyLong())).thenReturn(Mono.just(existingTransaction));
        when(accountRepositoryPort.findByAccountNumber(anyString())).thenReturn(Mono.just(account));
        when(accountRepositoryPort.save(any(Account.class))).thenReturn(Mono.just(account));
        when(transactionRepositoryPort.save(any(Transaction.class))).thenReturn(Mono.just(updatedTransaction));

        StepVerifier.create(updateTransactionUseCase.execute(command))
            .expectNextMatches(t -> 
                t.getAmount().compareTo(new BigDecimal("300.00")) == 0 &&
                t.getBalance().compareTo(new BigDecimal("800.00")) == 0
            )
            .verifyComplete();
    }

    @Test
    void execute_shouldThrowInsufficientBalanceException_whenUpdateCausesNegativeBalance() {
        UpdateTransactionCommand command = new UpdateTransactionCommand(
            1L,
            "Withdrawal",
            new BigDecimal("-2000.00")
        );

        when(transactionRepositoryPort.findById(anyLong())).thenReturn(Mono.just(existingTransaction));
        when(accountRepositoryPort.findByAccountNumber(anyString())).thenReturn(Mono.just(account));

        StepVerifier.create(updateTransactionUseCase.execute(command))
            .expectError(InsufficientBalanceException.class)
            .verify();
    }

    @Test
    void execute_shouldThrowTransactionNotFoundException_whenTransactionDoesNotExist() {
        UpdateTransactionCommand command = new UpdateTransactionCommand(
            999L,
            "Deposit",
            new BigDecimal("100.00")
        );

        when(transactionRepositoryPort.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(updateTransactionUseCase.execute(command))
            .expectError(TransactionNotFoundException.class)
            .verify();
    }
}
