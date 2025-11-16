package com.devsu.account_service.adapter.in.web;

import com.devsu.account_service.adapter.in.web.dto.ReportResponse;
import com.devsu.account_service.application.usecase.generatereport.GenerateReportQuery;
import com.devsu.account_service.domain.port.in.GenerateReportPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReportController {
    private final GenerateReportPort generateReportPort;
    
    @GetMapping
    public Mono<ResponseEntity<ReportResponse>> generateReport(
            @RequestParam String fecha,
            @RequestParam String cliente) {
        
        String[] dates = fecha.contains(",") ? fecha.split(",") : fecha.split("-");
        LocalDateTime startDate = LocalDate.parse(dates[0].trim()).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(dates[1].trim()).atTime(23, 59, 59);
        
        GenerateReportQuery query = new GenerateReportQuery(cliente, startDate, endDate);
        
        return generateReportPort.execute(query)
            .map(ResponseEntity::ok);
    }
}
