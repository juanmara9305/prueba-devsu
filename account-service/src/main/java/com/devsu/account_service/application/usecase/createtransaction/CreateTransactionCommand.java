package com.devsu.account_service.application.usecase.createtransaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionCommand {
    private String accountNumber;
    private String transactionType;
    private BigDecimal amount;
}
