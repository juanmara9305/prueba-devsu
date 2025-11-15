package com.devsu.person_service.domain.port.out;

import com.devsu.person_service.domain.model.Client;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientRepositoryPort {
    Mono<Client> save(Client client);
    Flux<Client> findAll();
    Mono<Client> findByClientId(String clientId);
    Mono<Boolean> existsByClientId(String clientId);
    Mono<Void> deleteByClientId(String clientId);
}
