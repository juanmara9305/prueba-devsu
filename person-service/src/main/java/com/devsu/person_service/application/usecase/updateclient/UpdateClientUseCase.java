package com.devsu.person_service.application.usecase.updateclient;

import com.devsu.person_service.application.service.PasswordHasher;
import com.devsu.person_service.domain.exception.ClientNotFoundException;
import com.devsu.person_service.domain.exception.InvalidPasswordException;
import com.devsu.person_service.domain.model.Client;
import com.devsu.person_service.domain.port.in.UpdateClientPort;
import com.devsu.person_service.domain.port.out.ClientRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UpdateClientUseCase implements UpdateClientPort {
    private final ClientRepositoryPort clientRepositoryPort;
    private final PasswordHasher passwordHasher;
    
    @Override
    public Mono<Client> execute(UpdateClientCommand command) {
        return clientRepositoryPort.findByClientId(command.getClientId())
            .switchIfEmpty(Mono.error(new ClientNotFoundException(
                command.getClientId(),
                "Client not found. Cannot update a non-existent client.")))
            .flatMap(existing -> {
                if (command.getRawPassword() != null) {
                    if (!Client.isValidPassword(command.getRawPassword())) {
                        return Mono.error(new InvalidPasswordException(
                            "Password must be at least 8 characters with 1 lowercase, 1 uppercase, and 1 digit"));
                    }
                    command.getClient().setPassword(passwordHasher.hash(command.getRawPassword()));
                } else {
                    command.getClient().setPassword(existing.getPassword());
                }
                
                command.getClient().setId(existing.getId());
                
                return clientRepositoryPort.save(command.getClient());
            });
    }
}
