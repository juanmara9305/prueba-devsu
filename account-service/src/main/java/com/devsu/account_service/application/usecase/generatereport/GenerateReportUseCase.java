package com.devsu.account_service.application.usecase.generatereport;

import com.devsu.account_service.adapter.in.web.dto.ReportResponse;
import com.devsu.account_service.adapter.in.web.mapper.ReportMapper;
import com.devsu.account_service.domain.exception.ClientNotFoundException;
import com.devsu.account_service.domain.port.in.GenerateReportPort;
import com.devsu.account_service.domain.port.out.AccountRepositoryPort;
import com.devsu.account_service.domain.port.out.TransactionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GenerateReportUseCase implements GenerateReportPort {
    private final AccountRepositoryPort accountRepositoryPort;
    private final TransactionRepositoryPort transactionRepositoryPort;
    private final ReportMapper reportMapper;
    
    @Override
    public Mono<ReportResponse> execute(GenerateReportQuery query) {
        return accountRepositoryPort.findByClientId(query.getClientId())
            .switchIfEmpty(Mono.error(new ClientNotFoundException(
                query.getClientId(),
                "No accounts found for this client")))
            .next()
            .flatMap(firstAccount -> 
                accountRepositoryPort.findByClientId(query.getClientId())
                    .flatMap(account -> 
                        transactionRepositoryPort.findByAccountNumberAndDateBetween(
                            account.getAccountNumber(),
                            query.getStartDate(),
                            query.getEndDate()
                        )
                        .collectList()
                        .map(transactions -> reportMapper.toAccountStatement(account, transactions))
                    )
                    .collectList()
                    .map(accountStatements -> reportMapper.toReportResponse(
                        firstAccount.getClientId(),
                        firstAccount.getClientName(),
                        accountStatements
                    ))
            );
    }
}
