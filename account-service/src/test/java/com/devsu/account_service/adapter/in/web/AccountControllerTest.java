package com.devsu.account_service.adapter.in.web;

import com.devsu.account_service.adapter.in.web.dto.AccountPatchRequest;
import com.devsu.account_service.adapter.in.web.dto.AccountRequest;
import com.devsu.account_service.adapter.in.web.dto.AccountResponse;
import com.devsu.account_service.adapter.in.web.mapper.AccountMapper;
import com.devsu.account_service.domain.exception.AccountAlreadyExistsException;
import com.devsu.account_service.domain.exception.AccountNotFoundException;
import com.devsu.account_service.domain.exception.InactiveClientException;
import com.devsu.account_service.domain.model.Account;
import com.devsu.account_service.domain.model.AccountType;
import com.devsu.account_service.domain.port.in.CreateAccountPort;
import com.devsu.account_service.domain.port.in.GetAccountByNumberPort;
import com.devsu.account_service.domain.port.in.GetAllAccountsPort;
import com.devsu.account_service.domain.port.in.PatchAccountPort;
import com.devsu.account_service.domain.port.in.UpdateAccountPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CreateAccountPort createAccountPort;

    @MockBean
    private GetAllAccountsPort getAllAccountsPort;

    @MockBean
    private GetAccountByNumberPort getAccountByNumberPort;

    @MockBean
    private UpdateAccountPort updateAccountPort;

    @MockBean
    private PatchAccountPort patchAccountPort;

    @MockBean
    private AccountMapper accountMapper;

    @Test
    void createAccount_shouldReturn201_whenAccountIsCreated() {
        AccountRequest request = new AccountRequest(
            "1234567890",
            "AHORROS",
            new BigDecimal("1000.00"),
            true,
            "client-1",
            "John Doe",
            true
        );

        Account account = new Account(
            1L,
            "1234567890",
            AccountType.AHORROS,
            new BigDecimal("1000.00"),
            true,
            "client-1",
            "John Doe",
            true
        );

        AccountResponse response = new AccountResponse(
            "1234567890",
            "AHORROS",
            new BigDecimal("1000.00"),
            true,
            "client-1",
            "John Doe"
        );

        when(accountMapper.toDomain(any(AccountRequest.class))).thenReturn(account);
        when(createAccountPort.execute(any())).thenReturn(Mono.just(account));
        when(accountMapper.toResponse(any(Account.class))).thenReturn(response);

        webTestClient.post()
            .uri("/cuentas")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.accountNumber").isEqualTo("1234567890")
            .jsonPath("$.accountType").isEqualTo("AHORROS")
            .jsonPath("$.balance").isEqualTo(1000.00)
            .jsonPath("$.status").isEqualTo(true)
            .jsonPath("$.clientId").isEqualTo("client-1")
            .jsonPath("$.clientName").isEqualTo("John Doe");
    }

    @Test
    void createAccount_shouldReturn400_whenValidationFails() {
        AccountRequest request = new AccountRequest(
            "",
            null,
            null,
            null,
            "",
            "",
            null
        );

        webTestClient.post()
            .uri("/cuentas")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void createAccount_shouldReturn409_whenAccountAlreadyExists() {
        AccountRequest request = new AccountRequest(
            "1234567890",
            "AHORROS",
            new BigDecimal("1000.00"),
            true,
            "client-1",
            "John Doe",
            true
        );

        Account account = new Account(
            null,
            "1234567890",
            AccountType.AHORROS,
            new BigDecimal("1000.00"),
            true,
            "client-1",
            "John Doe",
            true
        );

        when(accountMapper.toDomain(any(AccountRequest.class))).thenReturn(account);
        when(createAccountPort.execute(any())).thenReturn(
            Mono.error(new AccountAlreadyExistsException("1234567890", "Account already exists"))
        );

        webTestClient.post()
            .uri("/cuentas")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isEqualTo(409);
    }

    @Test
    void createAccount_shouldReturn400_whenClientIsInactive() {
        AccountRequest request = new AccountRequest(
            "1234567890",
            "AHORROS",
            new BigDecimal("1000.00"),
            true,
            "client-1",
            "John Doe",
            false
        );

        Account account = new Account(
            null,
            "1234567890",
            AccountType.AHORROS,
            new BigDecimal("1000.00"),
            true,
            "client-1",
            "John Doe",
            false
        );

        when(accountMapper.toDomain(any(AccountRequest.class))).thenReturn(account);
        when(createAccountPort.execute(any())).thenReturn(
            Mono.error(new InactiveClientException("client-1", "Client is inactive"))
        );

        webTestClient.post()
            .uri("/cuentas")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void getAllAccounts_shouldReturn200_withAccountList() {
        Account account1 = new Account(
            1L,
            "1234567890",
            AccountType.AHORROS,
            new BigDecimal("1000.00"),
            true,
            "client-1",
            "John Doe",
            true
        );

        Account account2 = new Account(
            2L,
            "0987654321",
            AccountType.CORRIENTE,
            new BigDecimal("2000.00"),
            true,
            "client-2",
            "Jane Smith",
            true
        );

        AccountResponse response1 = new AccountResponse(
            "1234567890",
            "AHORROS",
            new BigDecimal("1000.00"),
            true,
            "client-1",
            "John Doe"
        );

        AccountResponse response2 = new AccountResponse(
            "0987654321",
            "CORRIENTE",
            new BigDecimal("2000.00"),
            true,
            "client-2",
            "Jane Smith"
        );

        when(getAllAccountsPort.execute(null)).thenReturn(Flux.just(account1, account2));
        when(accountMapper.toResponse(account1)).thenReturn(response1);
        when(accountMapper.toResponse(account2)).thenReturn(response2);

        webTestClient.get()
            .uri("/cuentas")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(AccountResponse.class)
            .hasSize(2);
    }

    @Test
    void getAccountByNumber_shouldReturn200_whenAccountExists() {
        Account account = new Account(
            1L,
            "1234567890",
            AccountType.AHORROS,
            new BigDecimal("1000.00"),
            true,
            "client-1",
            "John Doe",
            true
        );

        AccountResponse response = new AccountResponse(
            "1234567890",
            "AHORROS",
            new BigDecimal("1000.00"),
            true,
            "client-1",
            "John Doe"
        );

        when(getAccountByNumberPort.execute(anyString())).thenReturn(Mono.just(account));
        when(accountMapper.toResponse(any(Account.class))).thenReturn(response);

        webTestClient.get()
            .uri("/cuentas/1234567890")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.accountNumber").isEqualTo("1234567890")
            .jsonPath("$.accountType").isEqualTo("AHORROS")
            .jsonPath("$.balance").isEqualTo(1000.00);
    }

    @Test
    void getAccountByNumber_shouldReturn404_whenAccountNotFound() {
        when(getAccountByNumberPort.execute(anyString())).thenReturn(
            Mono.error(new AccountNotFoundException("9999999999", "Account not found"))
        );

        webTestClient.get()
            .uri("/cuentas/9999999999")
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    void updateAccount_shouldReturn200_whenAccountIsUpdated() {
        AccountRequest request = new AccountRequest(
            "1234567890",
            "CORRIENTE",
            new BigDecimal("1500.00"),
            false,
            "client-1",
            "John Doe",
            true
        );

        Account account = new Account(
            1L,
            "1234567890",
            AccountType.CORRIENTE,
            new BigDecimal("1500.00"),
            false,
            "client-1",
            "John Doe",
            true
        );

        AccountResponse response = new AccountResponse(
            "1234567890",
            "CORRIENTE",
            new BigDecimal("1500.00"),
            false,
            "client-1",
            "John Doe"
        );

        when(accountMapper.toDomain(any(AccountRequest.class))).thenReturn(account);
        when(updateAccountPort.execute(any())).thenReturn(Mono.just(account));
        when(accountMapper.toResponse(any(Account.class))).thenReturn(response);

        webTestClient.put()
            .uri("/cuentas/1234567890")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.accountNumber").isEqualTo("1234567890")
            .jsonPath("$.accountType").isEqualTo("CORRIENTE")
            .jsonPath("$.status").isEqualTo(false);
    }

    @Test
    void updateAccount_shouldReturn404_whenAccountNotFound() {
        AccountRequest request = new AccountRequest(
            "9999999999",
            "AHORROS",
            new BigDecimal("1000.00"),
            true,
            "client-1",
            "John Doe",
            true
        );

        Account account = new Account(
            null,
            "9999999999",
            AccountType.AHORROS,
            new BigDecimal("1000.00"),
            true,
            "client-1",
            "John Doe",
            true
        );

        when(accountMapper.toDomain(any(AccountRequest.class))).thenReturn(account);
        when(updateAccountPort.execute(any())).thenReturn(
            Mono.error(new AccountNotFoundException("9999999999", "Account not found"))
        );

        webTestClient.put()
            .uri("/cuentas/9999999999")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    void patchAccount_shouldReturn200_whenAccountIsPatched() {
        AccountPatchRequest request = new AccountPatchRequest(
            "CORRIENTE",
            false
        );

        Account accountPartial = new Account(
            null,
            null,
            AccountType.CORRIENTE,
            null,
            false,
            null,
            null,
            null
        );

        Account updatedAccount = new Account(
            1L,
            "1234567890",
            AccountType.CORRIENTE,
            new BigDecimal("1000.00"),
            false,
            "client-1",
            "John Doe",
            true
        );

        AccountResponse response = new AccountResponse(
            "1234567890",
            "CORRIENTE",
            new BigDecimal("1000.00"),
            false,
            "client-1",
            "John Doe"
        );

        when(accountMapper.toDomainPartial(any(AccountPatchRequest.class))).thenReturn(accountPartial);
        when(patchAccountPort.execute(any())).thenReturn(Mono.just(updatedAccount));
        when(accountMapper.toResponse(any(Account.class))).thenReturn(response);

        webTestClient.patch()
            .uri("/cuentas/1234567890")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.accountNumber").isEqualTo("1234567890")
            .jsonPath("$.accountType").isEqualTo("CORRIENTE")
            .jsonPath("$.status").isEqualTo(false);
    }

    @Test
    void patchAccount_shouldReturn404_whenAccountNotFound() {
        AccountPatchRequest request = new AccountPatchRequest(
            "CORRIENTE",
            false
        );

        Account accountPartial = new Account(
            null,
            null,
            AccountType.CORRIENTE,
            null,
            false,
            null,
            null,
            null
        );

        when(accountMapper.toDomainPartial(any(AccountPatchRequest.class))).thenReturn(accountPartial);
        when(patchAccountPort.execute(any())).thenReturn(
            Mono.error(new AccountNotFoundException("9999999999", "Account not found"))
        );

        webTestClient.patch()
            .uri("/cuentas/9999999999")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isNotFound();
    }
}
