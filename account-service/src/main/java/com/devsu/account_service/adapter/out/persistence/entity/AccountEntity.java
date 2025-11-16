package com.devsu.account_service.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("account")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {
    @Id
    private Long id;
    
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private Boolean status;
    private String clientId;
    private String clientName;
    private Boolean clientStatus;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
