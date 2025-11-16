package com.devsu.account_service.application.usecase.updatetransaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTransactionCommand {
    private Long id;
    private String transactionType;
    private BigDecimal amount;
}
