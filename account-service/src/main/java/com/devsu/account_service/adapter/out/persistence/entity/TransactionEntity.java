package com.devsu.account_service.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {
    @Id
    private Long id;
    
    private LocalDateTime date;
    private String transactionType;
    private BigDecimal amount;
    private BigDecimal balance;
    private String accountNumber;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
