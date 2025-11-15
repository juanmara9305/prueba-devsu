package com.devsu.person_service.domain.port.in;

import com.devsu.person_service.domain.model.Client;
import com.devsu.person_service.domain.usecase.UseCase;
import com.devsu.person_service.application.usecase.patchclient.PatchClientCommand;
import reactor.core.publisher.Mono;

public interface PatchClientPort extends UseCase<PatchClientCommand, Mono<Client>> {
}
