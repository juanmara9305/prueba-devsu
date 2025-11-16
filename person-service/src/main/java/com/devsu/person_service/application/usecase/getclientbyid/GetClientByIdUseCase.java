package com.devsu.person_service.application.usecase.getclientbyid;

import com.devsu.person_service.domain.exception.ClientNotFoundException;
import com.devsu.person_service.domain.model.Client;
import com.devsu.person_service.domain.port.in.GetClientByIdPort;
import com.devsu.person_service.domain.port.out.ClientRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GetClientByIdUseCase implements GetClientByIdPort {
    private final ClientRepositoryPort clientRepositoryPort;
    
    @Override
    public Mono<Client> execute(String clientId) {
        return clientRepositoryPort.findByClientId(clientId)
            .switchIfEmpty(Mono.error(new ClientNotFoundException(
                clientId,
                "Client not found. Please verify the client ID and try again.")));
    }
}
