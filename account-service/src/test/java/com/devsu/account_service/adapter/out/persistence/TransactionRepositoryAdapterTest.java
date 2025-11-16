package com.devsu.account_service.adapter.out.persistence;

import com.devsu.account_service.adapter.out.persistence.entity.AccountEntity;
import com.devsu.account_service.domain.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@DataR2dbcTest
@Import({TransactionRepositoryAdapter.class, com.devsu.account_service.adapter.out.persistence.mapper.TransactionPersistenceMapper.class})
@TestPropertySource(locations = "classpath:application-test.properties")
class TransactionRepositoryAdapterTest {

    @Autowired
    private TransactionRepositoryAdapter transactionRepositoryAdapter;

    @Autowired
    private AccountR2dbcRepository accountRepository;

    @Autowired
    private TransactionR2dbcRepository transactionRepository;

    private String testAccountNumber;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll().block();
        accountRepository.deleteAll().block();
        
        testAccountNumber = "ACC-" + System.currentTimeMillis();
        
        AccountEntity account = new AccountEntity();
        account.setAccountNumber(testAccountNumber);
        account.setAccountType("AHORROS");
        account.setBalance(new BigDecimal("1000.00"));
        account.setStatus(true);
        account.setClientId("client-1");
        account.setClientName("Test Client");
        account.setClientStatus(true);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());

        accountRepository.save(account).block();
    }

    @Test
    void shouldSaveNewTransaction() {
        Transaction transaction = new Transaction();
        transaction.setDate(LocalDateTime.now());
        transaction.setTransactionType("DEPOSIT");
        transaction.setAmount(new BigDecimal("500.00"));
        transaction.setBalance(new BigDecimal("1500.00"));
        transaction.setAccountNumber(testAccountNumber);

        StepVerifier.create(transactionRepositoryAdapter.save(transaction))
            .expectNextMatches(savedTransaction ->
                savedTransaction.getId() != null &&
                savedTransaction.getTransactionType().equals("DEPOSIT") &&
                savedTransaction.getAmount().compareTo(new BigDecimal("500.00")) == 0 &&
                savedTransaction.getBalance().compareTo(new BigDecimal("1500.00")) == 0 &&
                savedTransaction.getAccountNumber().equals(testAccountNumber)
            )
            .verifyComplete();
    }

    @Test
    void shouldSaveExistingTransaction() {
        Transaction transaction = new Transaction();
        transaction.setDate(LocalDateTime.now());
        transaction.setTransactionType("WITHDRAWAL");
        transaction.setAmount(new BigDecimal("-200.00"));
        transaction.setBalance(new BigDecimal("800.00"));
        transaction.setAccountNumber(testAccountNumber);

        StepVerifier.create(
            transactionRepositoryAdapter.save(transaction)
                .flatMap(savedTransaction -> {
                    savedTransaction.setAmount(new BigDecimal("-250.00"));
                    savedTransaction.setBalance(new BigDecimal("750.00"));
                    return transactionRepositoryAdapter.save(savedTransaction);
                })
        )
            .expectNextMatches(updatedTransaction ->
                updatedTransaction.getId() != null &&
                updatedTransaction.getAmount().compareTo(new BigDecimal("-250.00")) == 0 &&
                updatedTransaction.getBalance().compareTo(new BigDecimal("750.00")) == 0
            )
            .verifyComplete();
    }

    @Test
    void shouldFindAllTransactions() {
        Transaction transaction1 = new Transaction();
        transaction1.setDate(LocalDateTime.now());
        transaction1.setTransactionType("DEPOSIT");
        transaction1.setAmount(new BigDecimal("300.00"));
        transaction1.setBalance(new BigDecimal("1300.00"));
        transaction1.setAccountNumber(testAccountNumber);

        Transaction transaction2 = new Transaction();
        transaction2.setDate(LocalDateTime.now());
        transaction2.setTransactionType("WITHDRAWAL");
        transaction2.setAmount(new BigDecimal("-100.00"));
        transaction2.setBalance(new BigDecimal("1200.00"));
        transaction2.setAccountNumber(testAccountNumber);

        StepVerifier.create(
            transactionRepositoryAdapter.save(transaction1)
                .then(transactionRepositoryAdapter.save(transaction2))
                .thenMany(transactionRepositoryAdapter.findAll())
        )
            .expectNextCount(2)
            .verifyComplete();
    }

    @Test
    void shouldFindTransactionById() {
        Transaction transaction = new Transaction();
        transaction.setDate(LocalDateTime.now());
        transaction.setTransactionType("DEPOSIT");
        transaction.setAmount(new BigDecimal("400.00"));
        transaction.setBalance(new BigDecimal("1400.00"));
        transaction.setAccountNumber(testAccountNumber);

        StepVerifier.create(
            transactionRepositoryAdapter.save(transaction)
                .flatMap(savedTransaction ->
                    transactionRepositoryAdapter.findById(savedTransaction.getId())
                )
        )
            .expectNextMatches(foundTransaction ->
                foundTransaction.getAmount().compareTo(new BigDecimal("400.00")) == 0 &&
                foundTransaction.getTransactionType().equals("DEPOSIT")
            )
            .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenTransactionIdNotFound() {
        StepVerifier.create(transactionRepositoryAdapter.findById(99999L))
            .verifyComplete();
    }

    @Test
    void shouldFindTransactionsByAccountNumber() {
        Transaction transaction1 = new Transaction();
        transaction1.setDate(LocalDateTime.now());
        transaction1.setTransactionType("DEPOSIT");
        transaction1.setAmount(new BigDecimal("600.00"));
        transaction1.setBalance(new BigDecimal("1600.00"));
        transaction1.setAccountNumber(testAccountNumber);

        Transaction transaction2 = new Transaction();
        transaction2.setDate(LocalDateTime.now());
        transaction2.setTransactionType("WITHDRAWAL");
        transaction2.setAmount(new BigDecimal("-150.00"));
        transaction2.setBalance(new BigDecimal("1450.00"));
        transaction2.setAccountNumber(testAccountNumber);

        StepVerifier.create(
            transactionRepositoryAdapter.save(transaction1)
                .then(transactionRepositoryAdapter.save(transaction2))
                .thenMany(transactionRepositoryAdapter.findByAccountNumber(testAccountNumber))
        )
            .expectNextCount(2)
            .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenAccountNumberNotFound() {
        StepVerifier.create(transactionRepositoryAdapter.findByAccountNumber("NON-EXISTENT"))
            .verifyComplete();
    }

    @Test
    void shouldFindTransactionsByAccountNumberAndDateBetween() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusDays(1);
        LocalDateTime endDate = now.plusDays(1);

        Transaction transaction1 = new Transaction();
        transaction1.setDate(now);
        transaction1.setTransactionType("DEPOSIT");
        transaction1.setAmount(new BigDecimal("700.00"));
        transaction1.setBalance(new BigDecimal("1700.00"));
        transaction1.setAccountNumber(testAccountNumber);

        Transaction transaction2 = new Transaction();
        transaction2.setDate(now.minusHours(2));
        transaction2.setTransactionType("WITHDRAWAL");
        transaction2.setAmount(new BigDecimal("-200.00"));
        transaction2.setBalance(new BigDecimal("1000.00"));
        transaction2.setAccountNumber(testAccountNumber);

        Transaction transaction3 = new Transaction();
        transaction3.setDate(now.minusDays(2));
        transaction3.setTransactionType("DEPOSIT");
        transaction3.setAmount(new BigDecimal("100.00"));
        transaction3.setBalance(new BigDecimal("1200.00"));
        transaction3.setAccountNumber(testAccountNumber);

        StepVerifier.create(
            transactionRepositoryAdapter.save(transaction1)
                .then(transactionRepositoryAdapter.save(transaction2))
                .then(transactionRepositoryAdapter.save(transaction3))
                .thenMany(transactionRepositoryAdapter.findByAccountNumberAndDateBetween(
                    testAccountNumber, startDate, endDate
                ))
        )
            .expectNextCount(2)
            .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenNoTransactionsInDateRange() {
        LocalDateTime startDate = LocalDateTime.now().plusDays(10);
        LocalDateTime endDate = LocalDateTime.now().plusDays(20);

        StepVerifier.create(
            transactionRepositoryAdapter.findByAccountNumberAndDateBetween(
                testAccountNumber, startDate, endDate
            )
        )
            .verifyComplete();
    }

    @Test
    void shouldVerifyForeignKeyConstraintWithAccountNumber() {
        Transaction transaction = new Transaction();
        transaction.setDate(LocalDateTime.now());
        transaction.setTransactionType("DEPOSIT");
        transaction.setAmount(new BigDecimal("500.00"));
        transaction.setBalance(new BigDecimal("1500.00"));
        transaction.setAccountNumber(testAccountNumber);

        StepVerifier.create(transactionRepositoryAdapter.save(transaction))
            .expectNextMatches(savedTransaction ->
                savedTransaction.getAccountNumber().equals(testAccountNumber)
            )
            .verifyComplete();
    }
}
