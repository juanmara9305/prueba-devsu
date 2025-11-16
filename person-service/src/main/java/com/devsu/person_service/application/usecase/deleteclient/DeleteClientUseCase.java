package com.devsu.person_service.application.usecase.deleteclient;

import com.devsu.person_service.domain.exception.ClientNotFoundException;
import com.devsu.person_service.domain.port.in.DeleteClientPort;
import com.devsu.person_service.domain.port.out.ClientRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DeleteClientUseCase implements DeleteClientPort {
    private final ClientRepositoryPort clientRepositoryPort;
    
    @Override
    public Mono<Void> execute(String clientId) {
        return clientRepositoryPort.findByClientId(clientId)
            .switchIfEmpty(Mono.error(new ClientNotFoundException(
                clientId,
                "Client not found. Cannot delete a non-existent client.")))
            .flatMap(client -> clientRepositoryPort.deleteByClientId(clientId));
    }
}
