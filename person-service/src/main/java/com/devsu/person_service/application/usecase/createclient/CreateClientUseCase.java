package com.devsu.person_service.application.usecase.createclient;

import com.devsu.person_service.application.service.PasswordHasher;
import com.devsu.person_service.domain.exception.ClientAlreadyExistsException;
import com.devsu.person_service.domain.exception.InvalidPasswordException;
import com.devsu.person_service.domain.model.Client;
import com.devsu.person_service.domain.port.in.CreateClientPort;
import com.devsu.person_service.domain.port.out.ClientRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CreateClientUseCase implements CreateClientPort {
    private final ClientRepositoryPort clientRepositoryPort;
    private final PasswordHasher passwordHasher;
    
    @Override
    public Mono<Client> execute(CreateClientCommand command) {
        if (!Client.isValidPassword(command.getRawPassword())) {
            return Mono.error(new InvalidPasswordException(
                "Password must be at least 8 characters with 1 lowercase, 1 uppercase, and 1 digit"));
        }
        
        Client client = command.getClient();
        client.setPassword(passwordHasher.hash(command.getRawPassword()));
        
        return clientRepositoryPort.save(client);
    }
}
