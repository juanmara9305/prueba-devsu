package com.devsu.person_service.application.usecase.getallclients;

import com.devsu.person_service.domain.model.Client;
import com.devsu.person_service.domain.port.in.GetAllClientsPort;
import com.devsu.person_service.domain.port.out.ClientRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class GetAllClientsUseCase implements GetAllClientsPort {
    private final ClientRepositoryPort clientRepositoryPort;
    
    @Override
    public Flux<Client> execute(Void input) {
        return clientRepositoryPort.findAll();
    }
}
