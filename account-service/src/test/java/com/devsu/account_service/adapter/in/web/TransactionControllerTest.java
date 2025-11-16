package com.devsu.account_service.adapter.in.web;

import com.devsu.account_service.adapter.in.web.dto.TransactionRequest;
import com.devsu.account_service.adapter.in.web.dto.TransactionResponse;
import com.devsu.account_service.adapter.in.web.mapper.TransactionMapper;
import com.devsu.account_service.domain.exception.AccountNotFoundException;
import com.devsu.account_service.domain.exception.InsufficientBalanceException;
import com.devsu.account_service.domain.exception.TransactionNotFoundException;
import com.devsu.account_service.domain.model.Transaction;
import com.devsu.account_service.domain.port.in.CreateTransactionPort;
import com.devsu.account_service.domain.port.in.GetAllTransactionsPort;
import com.devsu.account_service.domain.port.in.GetTransactionByIdPort;
import com.devsu.account_service.domain.port.in.UpdateTransactionPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@WebFluxTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CreateTransactionPort createTransactionPort;

    @MockBean
    private GetAllTransactionsPort getAllTransactionsPort;

    @MockBean
    private GetTransactionByIdPort getTransactionByIdPort;

    @MockBean
    private UpdateTransactionPort updateTransactionPort;

    @MockBean
    private TransactionMapper transactionMapper;

    @Test
    void createTransaction_shouldReturn201_whenTransactionIsCreated() {
        TransactionRequest request = new TransactionRequest(
            "1234567890",
            "Deposito",
            new BigDecimal("500.00")
        );

        Transaction transaction = new Transaction(
            1L,
            LocalDateTime.now(),
            "Deposito",
            new BigDecimal("500.00"),
            new BigDecimal("1500.00"),
            "1234567890"
        );

        TransactionResponse response = new TransactionResponse(
            1L,
            transaction.getDate(),
            "Deposito",
            new BigDecimal("500.00"),
            new BigDecimal("1500.00"),
            "1234567890"
        );

        when(createTransactionPort.execute(any())).thenReturn(Mono.just(transaction));
        when(transactionMapper.toResponse(any(Transaction.class))).thenReturn(response);

        webTestClient.post()
            .uri("/movimientos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.id").isEqualTo(1)
            .jsonPath("$.transactionType").isEqualTo("Deposito")
            .jsonPath("$.amount").isEqualTo(500.00)
            .jsonPath("$.balance").isEqualTo(1500.00)
            .jsonPath("$.accountNumber").isEqualTo("1234567890");
    }

    @Test
    void createTransaction_shouldReturn400_whenValidationFails() {
        TransactionRequest request = new TransactionRequest(
            "",
            "",
            null
        );

        webTestClient.post()
            .uri("/movimientos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void createTransaction_shouldReturn404_whenAccountNotFound() {
        TransactionRequest request = new TransactionRequest(
            "9999999999",
            "Deposito",
            new BigDecimal("500.00")
        );

        when(createTransactionPort.execute(any())).thenReturn(
            Mono.error(new AccountNotFoundException("9999999999", "Account not found"))
        );

        webTestClient.post()
            .uri("/movimientos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    void createTransaction_shouldReturn400_whenInsufficientBalance() {
        TransactionRequest request = new TransactionRequest(
            "1234567890",
            "Retiro",
            new BigDecimal("-2000.00")
        );

        when(createTransactionPort.execute(any())).thenReturn(
            Mono.error(new InsufficientBalanceException("Saldo no disponible"))
        );

        webTestClient.post()
            .uri("/movimientos")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void getAllTransactions_shouldReturn200_withTransactionList() {
        Transaction transaction1 = new Transaction(
            1L,
            LocalDateTime.now(),
            "Deposito",
            new BigDecimal("500.00"),
            new BigDecimal("1500.00"),
            "1234567890"
        );

        Transaction transaction2 = new Transaction(
            2L,
            LocalDateTime.now(),
            "Retiro",
            new BigDecimal("-200.00"),
            new BigDecimal("1300.00"),
            "1234567890"
        );

        TransactionResponse response1 = new TransactionResponse(
            1L,
            transaction1.getDate(),
            "Deposito",
            new BigDecimal("500.00"),
            new BigDecimal("1500.00"),
            "1234567890"
        );

        TransactionResponse response2 = new TransactionResponse(
            2L,
            transaction2.getDate(),
            "Retiro",
            new BigDecimal("-200.00"),
            new BigDecimal("1300.00"),
            "1234567890"
        );

        when(getAllTransactionsPort.execute(null)).thenReturn(Flux.just(transaction1, transaction2));
        when(transactionMapper.toResponse(transaction1)).thenReturn(response1);
        when(transactionMapper.toResponse(transaction2)).thenReturn(response2);

        webTestClient.get()
            .uri("/movimientos")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(TransactionResponse.class)
            .hasSize(2);
    }

    @Test
    void getTransactionById_shouldReturn200_whenTransactionExists() {
        Transaction transaction = new Transaction(
            1L,
            LocalDateTime.now(),
            "Deposito",
            new BigDecimal("500.00"),
            new BigDecimal("1500.00"),
            "1234567890"
        );

        TransactionResponse response = new TransactionResponse(
            1L,
            transaction.getDate(),
            "Deposito",
            new BigDecimal("500.00"),
            new BigDecimal("1500.00"),
            "1234567890"
        );

        when(getTransactionByIdPort.execute(anyLong())).thenReturn(Mono.just(transaction));
        when(transactionMapper.toResponse(any(Transaction.class))).thenReturn(response);

        webTestClient.get()
            .uri("/movimientos/1")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").isEqualTo(1)
            .jsonPath("$.transactionType").isEqualTo("Deposito")
            .jsonPath("$.amount").isEqualTo(500.00)
            .jsonPath("$.balance").isEqualTo(1500.00);
    }

    @Test
    void getTransactionById_shouldReturn404_whenTransactionNotFound() {
        when(getTransactionByIdPort.execute(anyLong())).thenReturn(
            Mono.error(new TransactionNotFoundException(999L, "Transaction not found"))
        );

        webTestClient.get()
            .uri("/movimientos/999")
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    void updateTransaction_shouldReturn200_whenTransactionIsUpdated() {
        TransactionRequest request = new TransactionRequest(
            "1234567890",
            "Deposito",
            new BigDecimal("600.00")
        );

        Transaction transaction = new Transaction(
            1L,
            LocalDateTime.now(),
            "Deposito",
            new BigDecimal("600.00"),
            new BigDecimal("1600.00"),
            "1234567890"
        );

        TransactionResponse response = new TransactionResponse(
            1L,
            transaction.getDate(),
            "Deposito",
            new BigDecimal("600.00"),
            new BigDecimal("1600.00"),
            "1234567890"
        );

        when(updateTransactionPort.execute(any())).thenReturn(Mono.just(transaction));
        when(transactionMapper.toResponse(any(Transaction.class))).thenReturn(response);

        webTestClient.put()
            .uri("/movimientos/1")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").isEqualTo(1)
            .jsonPath("$.transactionType").isEqualTo("Deposito")
            .jsonPath("$.amount").isEqualTo(600.00)
            .jsonPath("$.balance").isEqualTo(1600.00);
    }

    @Test
    void updateTransaction_shouldReturn404_whenTransactionNotFound() {
        TransactionRequest request = new TransactionRequest(
            "1234567890",
            "Deposito",
            new BigDecimal("600.00")
        );

        when(updateTransactionPort.execute(any())).thenReturn(
            Mono.error(new TransactionNotFoundException(999L, "Transaction not found"))
        );

        webTestClient.put()
            .uri("/movimientos/999")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    void updateTransaction_shouldReturn400_whenInsufficientBalance() {
        TransactionRequest request = new TransactionRequest(
            "1234567890",
            "Retiro",
            new BigDecimal("-5000.00")
        );

        when(updateTransactionPort.execute(any())).thenReturn(
            Mono.error(new InsufficientBalanceException("Saldo no disponible"))
        );

        webTestClient.put()
            .uri("/movimientos/1")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest();
    }
}
