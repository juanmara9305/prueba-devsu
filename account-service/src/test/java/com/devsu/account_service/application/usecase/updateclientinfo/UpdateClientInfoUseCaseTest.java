package com.devsu.account_service.application.usecase.updateclientinfo;

import com.devsu.account_service.domain.model.Account;
import com.devsu.account_service.domain.model.AccountType;
import com.devsu.account_service.domain.port.out.AccountRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateClientInfoUseCaseTest {

    @Mock
    private AccountRepositoryPort accountRepositoryPort;

    @InjectMocks
    private UpdateClientInfoUseCase updateClientInfoUseCase;

    private Account account1;
    private Account account2;

    @BeforeEach
    void setUp() {
        account1 = new Account(
            1L,
            "1234567890",
            AccountType.AHORROS,
            new BigDecimal("1000.00"),
            true,
            "client-1",
            "Old Name",
            true
        );

        account2 = new Account(
            2L,
            "0987654321",
            AccountType.CORRIENTE,
            new BigDecimal("2000.00"),
            true,
            "client-1",
            "Old Name",
            true
        );
    }

    @Test
    void execute_shouldUpdateClientInfo_whenClientHasAccounts() {
        UpdateClientInfoCommand command = new UpdateClientInfoCommand(
            "client-1",
            "Updated Name",
            false
        );

        Account updatedAccount1 = new Account(
            1L,
            "1234567890",
            AccountType.AHORROS,
            new BigDecimal("1000.00"),
            true,
            "client-1",
            "Updated Name",
            false
        );

        Account updatedAccount2 = new Account(
            2L,
            "0987654321",
            AccountType.CORRIENTE,
            new BigDecimal("2000.00"),
            true,
            "client-1",
            "Updated Name",
            false
        );

        when(accountRepositoryPort.findByClientId(anyString()))
            .thenReturn(Flux.just(account1, account2));
        when(accountRepositoryPort.save(any(Account.class)))
            .thenReturn(Mono.just(updatedAccount1))
            .thenReturn(Mono.just(updatedAccount2));

        StepVerifier.create(updateClientInfoUseCase.execute(command))
            .verifyComplete();

        verify(accountRepositoryPort, times(2)).save(any(Account.class));
    }

    @Test
    void execute_shouldUpdateMultipleAccounts_whenClientHasMultipleAccounts() {
        UpdateClientInfoCommand command = new UpdateClientInfoCommand(
            "client-1",
            "New Client Name",
            true
        );

        Account updatedAccount1 = new Account(
            1L,
            "1234567890",
            AccountType.AHORROS,
            new BigDecimal("1000.00"),
            true,
            "client-1",
            "New Client Name",
            true
        );

        Account updatedAccount2 = new Account(
            2L,
            "0987654321",
            AccountType.CORRIENTE,
            new BigDecimal("2000.00"),
            true,
            "client-1",
            "New Client Name",
            true
        );

        when(accountRepositoryPort.findByClientId(anyString()))
            .thenReturn(Flux.just(account1, account2));
        when(accountRepositoryPort.save(any(Account.class)))
            .thenReturn(Mono.just(updatedAccount1))
            .thenReturn(Mono.just(updatedAccount2));

        StepVerifier.create(updateClientInfoUseCase.execute(command))
            .verifyComplete();

        verify(accountRepositoryPort, times(2)).save(any(Account.class));
    }

    @Test
    void execute_shouldCompleteSuccessfully_whenNoAccountsFound() {
        UpdateClientInfoCommand command = new UpdateClientInfoCommand(
            "client-999",
            "Non Existent Client",
            true
        );

        when(accountRepositoryPort.findByClientId(anyString())).thenReturn(Flux.empty());

        StepVerifier.create(updateClientInfoUseCase.execute(command))
            .verifyComplete();
    }

    @Test
    void execute_shouldHandleError_whenSaveFails() {
        UpdateClientInfoCommand command = new UpdateClientInfoCommand(
            "client-1",
            "Updated Name",
            true
        );

        when(accountRepositoryPort.findByClientId(anyString()))
            .thenReturn(Flux.just(account1));
        when(accountRepositoryPort.save(any(Account.class)))
            .thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(updateClientInfoUseCase.execute(command))
            .verifyComplete();
    }
}
