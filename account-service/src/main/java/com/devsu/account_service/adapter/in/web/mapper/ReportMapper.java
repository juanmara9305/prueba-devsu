package com.devsu.account_service.adapter.in.web.mapper;

import com.devsu.account_service.adapter.in.web.dto.AccountStatementDto;
import com.devsu.account_service.adapter.in.web.dto.ReportResponse;
import com.devsu.account_service.adapter.in.web.dto.TransactionStatementDto;
import com.devsu.account_service.domain.model.Account;
import com.devsu.account_service.domain.model.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReportMapper {
    
    public AccountStatementDto toAccountStatement(Account account, List<Transaction> transactions) {
        List<TransactionStatementDto> transactionStatements = transactions.stream()
            .map(this::toTransactionStatement)
            .collect(Collectors.toList());
        
        return new AccountStatementDto(
            account.getAccountNumber(),
            account.getAccountType().name(),
            account.getBalance(),
            account.getStatus(),
            transactionStatements
        );
    }
    
    public TransactionStatementDto toTransactionStatement(Transaction transaction) {
        return new TransactionStatementDto(
            transaction.getDate(),
            transaction.getTransactionType(),
            transaction.getAmount(),
            transaction.getBalance()
        );
    }
    
    public ReportResponse toReportResponse(String clientId, String clientName, List<AccountStatementDto> accountStatements) {
        return new ReportResponse(clientId, clientName, accountStatements);
    }
}
