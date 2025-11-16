package com.devsu.account_service.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    private String clientId;
    private String clientName;
    private List<AccountStatementDto> accounts;
}
