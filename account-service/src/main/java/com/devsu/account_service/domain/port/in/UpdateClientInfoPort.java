package com.devsu.account_service.domain.port.in;

import com.devsu.account_service.application.usecase.updateclientinfo.UpdateClientInfoCommand;
import com.devsu.account_service.domain.usecase.UseCase;
import reactor.core.publisher.Mono;

public interface UpdateClientInfoPort extends UseCase<UpdateClientInfoCommand, Mono<Void>> {
}
