package com.devsu.account_service.application.usecase.createaccount;

import com.devsu.account_service.domain.exception.AccountAlreadyExistsException;
import com.devsu.account_service.domain.exception.InactiveClientException;
import com.devsu.account_service.domain.model.Account;
import com.devsu.account_service.domain.model.AccountType;
import com.devsu.account_service.domain.port.out.AccountRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateAccountUseCaseTest {

    @Mock
    private AccountRepositoryPort accountRepositoryPort;

    @InjectMocks
    private CreateAccountUseCase createAccountUseCase;

    private Account activeClientAccount;
    private Account inactiveClientAccount;

    @BeforeEach
    void setUp() {
        activeClientAccount = new Account(
            null,
            "1234567890",
            AccountType.AHORROS,
            new BigDecimal("1000.00"),
            true,
            "client-1",
            "John Doe",
            true
        );

        inactiveClientAccount = new Account(
            null,
            "9876543210",
            AccountType.CORRIENTE,
            new BigDecimal("500.00"),
            true,
            "client-2",
            "Jane Smith",
            false
        );
    }

    @Test
    void execute_shouldCreateAccount_whenClientIsActiveAndAccountNumberIsUnique() {
        CreateAccountCommand command = new CreateAccountCommand(activeClientAccount);

        when(accountRepositoryPort.existsByAccountNumber(anyString())).thenReturn(Mono.just(false));
        when(accountRepositoryPort.save(any(Account.class))).thenReturn(Mono.just(activeClientAccount));

        StepVerifier.create(createAccountUseCase.execute(command))
            .expectNext(activeClientAccount)
            .verifyComplete();
    }

    @Test
    void execute_shouldThrowInactiveClientException_whenClientIsInactive() {
        CreateAccountCommand command = new CreateAccountCommand(inactiveClientAccount);

        StepVerifier.create(createAccountUseCase.execute(command))
            .expectError(InactiveClientException.class)
            .verify();
    }

    @Test
    void execute_shouldThrowAccountAlreadyExistsException_whenAccountNumberExists() {
        CreateAccountCommand command = new CreateAccountCommand(activeClientAccount);

        when(accountRepositoryPort.existsByAccountNumber(anyString())).thenReturn(Mono.just(true));

        StepVerifier.create(createAccountUseCase.execute(command))
            .expectError(AccountAlreadyExistsException.class)
            .verify();
    }
}
