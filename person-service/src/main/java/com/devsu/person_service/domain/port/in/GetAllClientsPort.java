package com.devsu.person_service.domain.port.in;

import com.devsu.person_service.domain.model.Client;
import com.devsu.person_service.domain.usecase.UseCase;
import reactor.core.publisher.Flux;

public interface GetAllClientsPort extends UseCase<Void, Flux<Client>> {
}
