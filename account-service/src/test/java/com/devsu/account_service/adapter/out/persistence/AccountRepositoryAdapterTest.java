package com.devsu.account_service.adapter.out.persistence;

import com.devsu.account_service.domain.model.Account;
import com.devsu.account_service.domain.model.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

@DataR2dbcTest
@Import({AccountRepositoryAdapter.class, com.devsu.account_service.adapter.out.persistence.mapper.AccountPersistenceMapper.class})
@TestPropertySource(locations = "classpath:application-test.properties")
class AccountRepositoryAdapterTest {

    @Autowired
    private AccountRepositoryAdapter accountRepositoryAdapter;

    @Autowired
    private AccountR2dbcRepository accountRepository;

    @Autowired
    private TransactionR2dbcRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll().block();
        accountRepository.deleteAll().block();
    }

    @Test
    void shouldSaveNewAccount() {
        Account account = new Account();
        account.setAccountNumber("1234567890");
        account.setAccountType(AccountType.AHORROS);
        account.setBalance(new BigDecimal("1000.00"));
        account.setStatus(true);
        account.setClientId("client-1");
        account.setClientName("John Doe");
        account.setClientStatus(true);

        StepVerifier.create(accountRepositoryAdapter.save(account))
            .expectNextMatches(savedAccount ->
                savedAccount.getId() != null &&
                savedAccount.getAccountNumber().equals("1234567890") &&
                savedAccount.getAccountType() == AccountType.AHORROS &&
                savedAccount.getBalance().compareTo(new BigDecimal("1000.00")) == 0 &&
                savedAccount.getStatus() &&
                savedAccount.getClientId().equals("client-1") &&
                savedAccount.getClientName().equals("John Doe") &&
                savedAccount.getClientStatus()
            )
            .verifyComplete();
    }

    @Test
    void shouldSaveExistingAccount() {
        Account account = new Account();
        account.setAccountNumber("9876543210");
        account.setAccountType(AccountType.CORRIENTE);
        account.setBalance(new BigDecimal("2000.00"));
        account.setStatus(true);
        account.setClientId("client-2");
        account.setClientName("Jane Smith");
        account.setClientStatus(true);

        StepVerifier.create(
            accountRepositoryAdapter.save(account)
                .flatMap(savedAccount -> {
                    savedAccount.setBalance(new BigDecimal("2500.00"));
                    savedAccount.setStatus(false);
                    return accountRepositoryAdapter.save(savedAccount);
                })
        )
            .expectNextMatches(updatedAccount ->
                updatedAccount.getId() != null &&
                updatedAccount.getAccountNumber().equals("9876543210") &&
                updatedAccount.getBalance().compareTo(new BigDecimal("2500.00")) == 0 &&
                !updatedAccount.getStatus()
            )
            .verifyComplete();
    }

    @Test
    void shouldFindAllAccounts() {
        Account account1 = new Account();
        account1.setAccountNumber("1111111111");
        account1.setAccountType(AccountType.AHORROS);
        account1.setBalance(new BigDecimal("500.00"));
        account1.setStatus(true);
        account1.setClientId("client-3");
        account1.setClientName("Alice Brown");
        account1.setClientStatus(true);

        Account account2 = new Account();
        account2.setAccountNumber("2222222222");
        account2.setAccountType(AccountType.CORRIENTE);
        account2.setBalance(new BigDecimal("1500.00"));
        account2.setStatus(true);
        account2.setClientId("client-4");
        account2.setClientName("Bob Wilson");
        account2.setClientStatus(true);

        StepVerifier.create(
            accountRepositoryAdapter.save(account1)
                .then(accountRepositoryAdapter.save(account2))
                .thenMany(accountRepositoryAdapter.findAll())
        )
            .expectNextCount(2)
            .verifyComplete();
    }

    @Test
    void shouldFindAccountByAccountNumber() {
        Account account = new Account();
        account.setAccountNumber("3333333333");
        account.setAccountType(AccountType.AHORROS);
        account.setBalance(new BigDecimal("750.00"));
        account.setStatus(true);
        account.setClientId("client-5");
        account.setClientName("Charlie Davis");
        account.setClientStatus(true);

        StepVerifier.create(
            accountRepositoryAdapter.save(account)
                .then(accountRepositoryAdapter.findByAccountNumber("3333333333"))
        )
            .expectNextMatches(foundAccount ->
                foundAccount.getAccountNumber().equals("3333333333") &&
                foundAccount.getBalance().compareTo(new BigDecimal("750.00")) == 0
            )
            .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenAccountNumberNotFound() {
        StepVerifier.create(accountRepositoryAdapter.findByAccountNumber("9999999999"))
            .verifyComplete();
    }

    @Test
    void shouldCheckIfAccountNumberExists() {
        Account account = new Account();
        account.setAccountNumber("4444444444");
        account.setAccountType(AccountType.CORRIENTE);
        account.setBalance(new BigDecimal("1200.00"));
        account.setStatus(true);
        account.setClientId("client-6");
        account.setClientName("Diana Evans");
        account.setClientStatus(true);

        StepVerifier.create(
            accountRepositoryAdapter.save(account)
                .then(accountRepositoryAdapter.existsByAccountNumber("4444444444"))
        )
            .expectNext(true)
            .verifyComplete();
    }

    @Test
    void shouldReturnFalseWhenAccountNumberDoesNotExist() {
        StepVerifier.create(accountRepositoryAdapter.existsByAccountNumber("0000000000"))
            .expectNext(false)
            .verifyComplete();
    }

    @Test
    void shouldFindAccountsByClientId() {
        Account account1 = new Account();
        account1.setAccountNumber("5555555555");
        account1.setAccountType(AccountType.AHORROS);
        account1.setBalance(new BigDecimal("800.00"));
        account1.setStatus(true);
        account1.setClientId("client-7");
        account1.setClientName("Eve Foster");
        account1.setClientStatus(true);

        Account account2 = new Account();
        account2.setAccountNumber("6666666666");
        account2.setAccountType(AccountType.CORRIENTE);
        account2.setBalance(new BigDecimal("1800.00"));
        account2.setStatus(true);
        account2.setClientId("client-7");
        account2.setClientName("Eve Foster");
        account2.setClientStatus(true);

        StepVerifier.create(
            accountRepositoryAdapter.save(account1)
                .then(accountRepositoryAdapter.save(account2))
                .thenMany(accountRepositoryAdapter.findByClientId("client-7"))
        )
            .expectNextCount(2)
            .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenClientIdNotFound() {
        StepVerifier.create(accountRepositoryAdapter.findByClientId("non-existent-client"))
            .verifyComplete();
    }
}
