package com.devsu.account_service.domain.port.in;

import com.devsu.account_service.domain.model.Transaction;
import com.devsu.account_service.domain.usecase.UseCase;
import reactor.core.publisher.Mono;

public interface GetTransactionByIdPort extends UseCase<Long, Mono<Transaction>> {
}
