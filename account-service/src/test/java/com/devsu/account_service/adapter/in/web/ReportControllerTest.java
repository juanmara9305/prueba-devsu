package com.devsu.account_service.adapter.in.web;

import com.devsu.account_service.adapter.in.web.dto.AccountStatementDto;
import com.devsu.account_service.adapter.in.web.dto.ReportResponse;
import com.devsu.account_service.adapter.in.web.dto.TransactionStatementDto;
import com.devsu.account_service.domain.exception.ClientNotFoundException;
import com.devsu.account_service.domain.port.in.GenerateReportPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(ReportController.class)
class ReportControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GenerateReportPort generateReportPort;

    @Test
    void generateReport_shouldReturn200_withReportData() {
        TransactionStatementDto transaction1 = new TransactionStatementDto(
            LocalDateTime.of(2024, 1, 15, 10, 0),
            "Deposito",
            new BigDecimal("500.00"),
            new BigDecimal("1500.00")
        );

        TransactionStatementDto transaction2 = new TransactionStatementDto(
            LocalDateTime.of(2024, 1, 16, 14, 30),
            "Retiro",
            new BigDecimal("-200.00"),
            new BigDecimal("1300.00")
        );

        AccountStatementDto accountStatement = new AccountStatementDto(
            "1234567890",
            "AHORROS",
            new BigDecimal("1300.00"),
            true,
            List.of(transaction1, transaction2)
        );

        ReportResponse reportResponse = new ReportResponse(
            "client-1",
            "John Doe",
            List.of(accountStatement)
        );

        when(generateReportPort.execute(any())).thenReturn(Mono.just(reportResponse));

        webTestClient.get()
            .uri("/reportes?fecha=2024-01-01,2024-01-31&cliente=client-1")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.clientId").isEqualTo("client-1")
            .jsonPath("$.clientName").isEqualTo("John Doe")
            .jsonPath("$.accounts").isArray()
            .jsonPath("$.accounts[0].accountNumber").isEqualTo("1234567890")
            .jsonPath("$.accounts[0].accountType").isEqualTo("AHORROS")
            .jsonPath("$.accounts[0].currentBalance").isEqualTo(1300.00)
            .jsonPath("$.accounts[0].transactions").isArray()
            .jsonPath("$.accounts[0].transactions[0].transactionType").isEqualTo("Deposito")
            .jsonPath("$.accounts[0].transactions[0].amount").isEqualTo(500.00);
    }

    @Test
    void generateReport_shouldReturn200_withCommaSeparator() {
        TransactionStatementDto transaction = new TransactionStatementDto(
            LocalDateTime.of(2024, 1, 15, 10, 0),
            "Deposito",
            new BigDecimal("500.00"),
            new BigDecimal("1500.00")
        );

        AccountStatementDto accountStatement = new AccountStatementDto(
            "1234567890",
            "AHORROS",
            new BigDecimal("1500.00"),
            true,
            List.of(transaction)
        );

        ReportResponse reportResponse = new ReportResponse(
            "client-1",
            "John Doe",
            List.of(accountStatement)
        );

        when(generateReportPort.execute(any())).thenReturn(Mono.just(reportResponse));

        webTestClient.get()
            .uri("/reportes?fecha=2024-02-01,2024-02-28&cliente=client-1")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.clientId").isEqualTo("client-1")
            .jsonPath("$.clientName").isEqualTo("John Doe");
    }

    @Test
    void generateReport_shouldReturn404_whenClientNotFound() {
        when(generateReportPort.execute(any())).thenReturn(
            Mono.error(new ClientNotFoundException("client-999", "Client not found"))
        );

        webTestClient.get()
            .uri("/reportes?fecha=2024-01-01,2024-01-31&cliente=client-999")
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    void generateReport_shouldReturn200_withEmptyTransactions() {
        AccountStatementDto accountStatement = new AccountStatementDto(
            "1234567890",
            "AHORROS",
            new BigDecimal("1000.00"),
            true,
            List.of()
        );

        ReportResponse reportResponse = new ReportResponse(
            "client-1",
            "John Doe",
            List.of(accountStatement)
        );

        when(generateReportPort.execute(any())).thenReturn(Mono.just(reportResponse));

        webTestClient.get()
            .uri("/reportes?fecha=2024-01-01,2024-01-31&cliente=client-1")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.clientId").isEqualTo("client-1")
            .jsonPath("$.accounts[0].transactions").isEmpty();
    }

    @Test
    void generateReport_shouldReturn200_withMultipleAccounts() {
        TransactionStatementDto transaction1 = new TransactionStatementDto(
            LocalDateTime.of(2024, 1, 15, 10, 0),
            "Deposito",
            new BigDecimal("500.00"),
            new BigDecimal("1500.00")
        );

        TransactionStatementDto transaction2 = new TransactionStatementDto(
            LocalDateTime.of(2024, 1, 16, 14, 30),
            "Retiro",
            new BigDecimal("-100.00"),
            new BigDecimal("1900.00")
        );

        AccountStatementDto accountStatement1 = new AccountStatementDto(
            "1234567890",
            "AHORROS",
            new BigDecimal("1500.00"),
            true,
            List.of(transaction1)
        );

        AccountStatementDto accountStatement2 = new AccountStatementDto(
            "0987654321",
            "CORRIENTE",
            new BigDecimal("1900.00"),
            true,
            List.of(transaction2)
        );

        ReportResponse reportResponse = new ReportResponse(
            "client-1",
            "John Doe",
            List.of(accountStatement1, accountStatement2)
        );

        when(generateReportPort.execute(any())).thenReturn(Mono.just(reportResponse));

        webTestClient.get()
            .uri("/reportes?fecha=2024-01-01,2024-01-31&cliente=client-1")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.accounts").isArray()
            .jsonPath("$.accounts.length()").isEqualTo(2)
            .jsonPath("$.accounts[0].accountNumber").isEqualTo("1234567890")
            .jsonPath("$.accounts[1].accountNumber").isEqualTo("0987654321");
    }

    @Test
    void generateReport_shouldHandleDateRangeWithSpaces() {
        ReportResponse reportResponse = new ReportResponse(
            "client-1",
            "John Doe",
            List.of()
        );

        when(generateReportPort.execute(any())).thenReturn(Mono.just(reportResponse));

        webTestClient.get()
            .uri("/reportes?fecha=2024-01-01, 2024-01-31&cliente=client-1")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.clientId").isEqualTo("client-1");
    }
}
