package com.devsu.account_service.application.usecase.generatereport;

import com.devsu.account_service.adapter.in.web.dto.AccountStatementDto;
import com.devsu.account_service.adapter.in.web.dto.ReportResponse;
import com.devsu.account_service.adapter.in.web.mapper.ReportMapper;
import com.devsu.account_service.domain.exception.ClientNotFoundException;
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
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerateReportUseCaseTest {

    @Mock
    private AccountRepositoryPort accountRepositoryPort;

    @Mock
    private TransactionRepositoryPort transactionRepositoryPort;

    @Mock
    private ReportMapper reportMapper;

    @InjectMocks
    private GenerateReportUseCase generateReportUseCase;

    private Account account1;
    private Account account2;
    private Transaction transaction1;
    private Transaction transaction2;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @BeforeEach
    void setUp() {
        startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        endDate = LocalDateTime.of(2024, 12, 31, 23, 59);

        account1 = new Account(
            1L,
            "1234567890",
            AccountType.AHORROS,
            new BigDecimal("1000.00"),
            true,
            "client-1",
            "John Doe",
            true
        );

        account2 = new Account(
            2L,
            "0987654321",
            AccountType.CORRIENTE,
            new BigDecimal("2000.00"),
            true,
            "client-1",
            "John Doe",
            true
        );

        transaction1 = new Transaction(
            1L,
            LocalDateTime.of(2024, 6, 15, 10, 0),
            "Deposit",
            new BigDecimal("500.00"),
            new BigDecimal("1500.00"),
            "1234567890"
        );

        transaction2 = new Transaction(
            2L,
            LocalDateTime.of(2024, 7, 20, 14, 30),
            "Withdrawal",
            new BigDecimal("-200.00"),
            new BigDecimal("1800.00"),
            "0987654321"
        );
    }

    @Test
    void execute_shouldGenerateReport_whenClientHasAccounts() {
        GenerateReportQuery query = new GenerateReportQuery("client-1", startDate, endDate);

        AccountStatementDto statement1 = new AccountStatementDto(
            "1234567890",
            "AHORROS",
            new BigDecimal("1000.00"),
            true,
            Collections.emptyList()
        );

        AccountStatementDto statement2 = new AccountStatementDto(
            "0987654321",
            "CORRIENTE",
            new BigDecimal("2000.00"),
            true,
            Collections.emptyList()
        );

        ReportResponse expectedReport = new ReportResponse(
            "client-1",
            "John Doe",
            List.of(statement1, statement2)
        );

        when(accountRepositoryPort.findByClientId(anyString()))
            .thenReturn(Flux.just(account1, account2));
        when(transactionRepositoryPort.findByAccountNumberAndDateBetween(anyString(), any(), any()))
            .thenReturn(Flux.just(transaction1))
            .thenReturn(Flux.just(transaction2));
        when(reportMapper.toAccountStatement(any(Account.class), anyList()))
            .thenReturn(statement1)
            .thenReturn(statement2);
        when(reportMapper.toReportResponse(anyString(), anyString(), anyList()))
            .thenReturn(expectedReport);

        StepVerifier.create(generateReportUseCase.execute(query))
            .expectNextMatches(report -> 
                report.getClientId().equals("client-1") &&
                report.getClientName().equals("John Doe") &&
                report.getAccounts().size() == 2
            )
            .verifyComplete();
    }

    @Test
    void execute_shouldFilterTransactionsByDateRange_whenGeneratingReport() {
        GenerateReportQuery query = new GenerateReportQuery("client-1", startDate, endDate);

        AccountStatementDto statement = new AccountStatementDto(
            "1234567890",
            "AHORROS",
            new BigDecimal("1000.00"),
            true,
            Collections.emptyList()
        );

        ReportResponse expectedReport = new ReportResponse(
            "client-1",
            "John Doe",
            List.of(statement)
        );

        when(accountRepositoryPort.findByClientId(anyString()))
            .thenReturn(Flux.just(account1));
        when(transactionRepositoryPort.findByAccountNumberAndDateBetween(
            anyString(), 
            any(LocalDateTime.class), 
            any(LocalDateTime.class)
        )).thenReturn(Flux.just(transaction1));
        when(reportMapper.toAccountStatement(any(Account.class), anyList()))
            .thenReturn(statement);
        when(reportMapper.toReportResponse(anyString(), anyString(), anyList()))
            .thenReturn(expectedReport);

        StepVerifier.create(generateReportUseCase.execute(query))
            .expectNextMatches(report -> report.getAccounts().size() == 1)
            .verifyComplete();
    }

    @Test
    void execute_shouldHandleMultipleAccounts_whenClientHasMultipleAccounts() {
        GenerateReportQuery query = new GenerateReportQuery("client-1", startDate, endDate);

        AccountStatementDto statement1 = new AccountStatementDto(
            "1234567890",
            "AHORROS",
            new BigDecimal("1000.00"),
            true,
            Collections.emptyList()
        );

        AccountStatementDto statement2 = new AccountStatementDto(
            "0987654321",
            "CORRIENTE",
            new BigDecimal("2000.00"),
            true,
            Collections.emptyList()
        );

        ReportResponse expectedReport = new ReportResponse(
            "client-1",
            "John Doe",
            List.of(statement1, statement2)
        );

        when(accountRepositoryPort.findByClientId(anyString()))
            .thenReturn(Flux.just(account1, account2));
        when(transactionRepositoryPort.findByAccountNumberAndDateBetween(anyString(), any(), any()))
            .thenReturn(Flux.empty());
        when(reportMapper.toAccountStatement(any(Account.class), anyList()))
            .thenReturn(statement1)
            .thenReturn(statement2);
        when(reportMapper.toReportResponse(anyString(), anyString(), anyList()))
            .thenReturn(expectedReport);

        StepVerifier.create(generateReportUseCase.execute(query))
            .expectNextMatches(report -> report.getAccounts().size() == 2)
            .verifyComplete();
    }

    @Test
    void execute_shouldThrowClientNotFoundException_whenClientHasNoAccounts() {
        GenerateReportQuery query = new GenerateReportQuery("client-999", startDate, endDate);

        when(accountRepositoryPort.findByClientId(anyString())).thenReturn(Flux.empty());

        StepVerifier.create(generateReportUseCase.execute(query))
            .expectError(ClientNotFoundException.class)
            .verify();
    }
}
