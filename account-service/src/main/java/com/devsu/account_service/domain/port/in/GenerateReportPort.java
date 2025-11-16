package com.devsu.account_service.domain.port.in;

import com.devsu.account_service.adapter.in.web.dto.ReportResponse;
import com.devsu.account_service.application.usecase.generatereport.GenerateReportQuery;
import com.devsu.account_service.domain.usecase.UseCase;
import reactor.core.publisher.Mono;

public interface GenerateReportPort extends UseCase<GenerateReportQuery, Mono<ReportResponse>> {
}
