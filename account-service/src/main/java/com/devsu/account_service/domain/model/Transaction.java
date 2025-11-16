package com.devsu.account_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private Long id;
    private LocalDateTime date;
    private String transactionType;
    private BigDecimal amount;
    private BigDecimal balance;
    private String accountNumber;
    
    public boolean isDeposit() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public boolean isWithdrawal() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }
    
    public BigDecimal getAbsoluteAmount() {
        return amount.abs();
    }
}
