package com.devsu.person_service.domain.port.in;

import com.devsu.person_service.domain.model.Client;
import com.devsu.person_service.domain.usecase.UseCase;
import com.devsu.person_service.application.usecase.updateclient.UpdateClientCommand;
import reactor.core.publisher.Mono;

public interface UpdateClientPort extends UseCase<UpdateClientCommand, Mono<Client>> {
}
