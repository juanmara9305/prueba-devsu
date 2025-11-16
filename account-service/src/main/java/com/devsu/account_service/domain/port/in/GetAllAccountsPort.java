package com.devsu.account_service.domain.port.in;

import com.devsu.account_service.domain.model.Account;
import com.devsu.account_service.domain.usecase.UseCase;
import reactor.core.publisher.Flux;

public interface GetAllAccountsPort extends UseCase<Void, Flux<Account>> {
}
