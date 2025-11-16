package com.devsu.account_service.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private Boolean status;
    private String clientId;
    private String clientName;
}
