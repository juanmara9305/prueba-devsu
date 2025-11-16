package com.devsu.account_service.domain.port.in;

import com.devsu.account_service.application.usecase.patchaccount.PatchAccountCommand;
import com.devsu.account_service.domain.model.Account;
import com.devsu.account_service.domain.usecase.UseCase;
import reactor.core.publisher.Mono;

public interface PatchAccountPort extends UseCase<PatchAccountCommand, Mono<Account>> {
}
