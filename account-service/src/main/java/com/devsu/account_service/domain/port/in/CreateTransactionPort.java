package com.devsu.account_service.domain.port.in;

import com.devsu.account_service.application.usecase.createtransaction.CreateTransactionCommand;
import com.devsu.account_service.domain.model.Transaction;
import com.devsu.account_service.domain.usecase.UseCase;
import reactor.core.publisher.Mono;

public interface CreateTransactionPort extends UseCase<CreateTransactionCommand, Mono<Transaction>> {
}
