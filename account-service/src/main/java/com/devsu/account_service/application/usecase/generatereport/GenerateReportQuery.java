package com.devsu.account_service.application.usecase.generatereport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateReportQuery {
    private String clientId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
