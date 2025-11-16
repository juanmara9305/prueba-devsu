package com.devsu.account_service.application.usecase.createtransaction;

import com.devsu.account_service.domain.exception.AccountNotFoundException;
import com.devsu.account_service.domain.exception.InsufficientBalanceException;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTransactionUseCaseTest {

    @Mock
    private AccountRepositoryPort accountRepositoryPort;

    @Mock
    private TransactionRepositoryPort transactionRepositoryPort;

    @InjectMocks
    private CreateTransactionUseCase createTransactionUseCase;

    private Account account;
    private Transaction transaction;

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

        transaction = new Transaction(
            1L,
            LocalDateTime.now(),
            "Deposit",
            new BigDecimal("500.00"),
            new BigDecimal("1500.00"),
            "1234567890"
        );
    }

    @Test
    void execute_shouldCreateDepositTransaction_whenAmountIsPositive() {
        CreateTransactionCommand command = new CreateTransactionCommand(
            "1234567890",
            "Deposit",
            new BigDecimal("500.00")
        );

        when(accountRepositoryPort.findByAccountNumber(anyString())).thenReturn(Mono.just(account));
        when(accountRepositoryPort.save(any(Account.class))).thenReturn(Mono.just(account));
        when(transactionRepositoryPort.save(any(Transaction.class))).thenReturn(Mono.just(transaction));

        StepVerifier.create(createTransactionUseCase.execute(command))
            .expectNextMatches(t -> 
                t.getAmount().compareTo(new BigDecimal("500.00")) == 0 &&
                t.getAccountNumber().equals("1234567890")
            )
            .verifyComplete();
    }

    @Test
    void execute_shouldCreateWithdrawalTransaction_whenSufficientBalance() {
        CreateTransactionCommand command = new CreateTransactionCommand(
            "1234567890",
            "Withdrawal",
            new BigDecimal("-300.00")
        );

        Transaction withdrawalTransaction = new Transaction(
            2L,
            LocalDateTime.now(),
            "Withdrawal",
            new BigDecimal("-300.00"),
            new BigDecimal("700.00"),
            "1234567890"
        );

        when(accountRepositoryPort.findByAccountNumber(anyString())).thenReturn(Mono.just(account));
        when(accountRepositoryPort.save(any(Account.class))).thenReturn(Mono.just(account));
        when(transactionRepositoryPort.save(any(Transaction.class))).thenReturn(Mono.just(withdrawalTransaction));

        StepVerifier.create(createTransactionUseCase.execute(command))
            .expectNextMatches(t -> 
                t.getAmount().compareTo(new BigDecimal("-300.00")) == 0 &&
                t.getBalance().compareTo(new BigDecimal("700.00")) == 0
            )
            .verifyComplete();
    }

    @Test
    void execute_shouldThrowInsufficientBalanceException_whenWithdrawalExceedsBalance() {
        CreateTransactionCommand command = new CreateTransactionCommand(
            "1234567890",
            "Withdrawal",
            new BigDecimal("-1500.00")
        );

        when(accountRepositoryPort.findByAccountNumber(anyString())).thenReturn(Mono.just(account));

        StepVerifier.create(createTransactionUseCase.execute(command))
            .expectError(InsufficientBalanceException.class)
            .verify();
    }

    @Test
    void execute_shouldThrowAccountNotFoundException_whenAccountDoesNotExist() {
        CreateTransactionCommand command = new CreateTransactionCommand(
            "9999999999",
            "Deposit",
            new BigDecimal("100.00")
        );

        when(accountRepositoryPort.findByAccountNumber(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(createTransactionUseCase.execute(command))
            .expectError(AccountNotFoundException.class)
            .verify();
    }
}
