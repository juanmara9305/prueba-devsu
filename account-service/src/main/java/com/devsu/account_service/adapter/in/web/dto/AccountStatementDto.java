package com.devsu.account_service.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatementDto {
    private String accountNumber;
    private String accountType;
    private BigDecimal currentBalance;
    private Boolean status;
    private List<TransactionStatementDto> transactions;
}
