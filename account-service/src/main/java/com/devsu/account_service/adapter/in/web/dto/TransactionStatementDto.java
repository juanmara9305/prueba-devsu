package com.devsu.account_service.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionStatementDto {
    private LocalDateTime date;
    private String transactionType;
    private BigDecimal amount;
    private BigDecimal balance;
}
