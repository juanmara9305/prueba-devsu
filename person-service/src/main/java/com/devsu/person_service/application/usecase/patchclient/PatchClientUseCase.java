package com.devsu.person_service.application.usecase.patchclient;

import com.devsu.person_service.application.service.PasswordHasher;
import com.devsu.person_service.domain.exception.ClientNotFoundException;
import com.devsu.person_service.domain.exception.InvalidPasswordException;
import com.devsu.person_service.domain.model.Client;
import com.devsu.person_service.domain.port.in.PatchClientPort;
import com.devsu.person_service.domain.port.out.ClientRepositoryPort;
import com.devsu.person_service.domain.port.out.PublishClientEventPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PatchClientUseCase implements PatchClientPort {
    private final ClientRepositoryPort clientRepositoryPort;
    private final PasswordHasher passwordHasher;
    private final PublishClientEventPort publishClientEventPort;
    
    @Override
    public Mono<Client> execute(PatchClientCommand command) {
        return clientRepositoryPort.findByClientId(command.getClientId())
            .switchIfEmpty(Mono.error(new ClientNotFoundException(
                command.getClientId(),
                "Client not found. Cannot update a non-existent client.")))
            .flatMap(existing -> {
                Client partial = command.getClientPartial();
                
                if (partial.getName() != null) existing.setName(partial.getName());
                if (partial.getGender() != null) existing.setGender(partial.getGender());
                if (partial.getBirthDate() != null) existing.setBirthDate(partial.getBirthDate());
                if (partial.getIdentification() != null) existing.setIdentification(partial.getIdentification());
                if (partial.getAddress() != null) existing.setAddress(partial.getAddress());
                if (partial.getPhone() != null) existing.setPhone(partial.getPhone());
                
                if (command.getRawPassword() != null) {
                    if (!Client.isValidPassword(command.getRawPassword())) {
                        return Mono.error(new InvalidPasswordException(
                            "Password must be at least 8 characters with 1 lowercase, 1 uppercase, and 1 digit"));
                    }
                    existing.setPassword(passwordHasher.hash(command.getRawPassword()));
                }
                
                if (partial.getStatus() != null) existing.setStatus(partial.getStatus());
                
                return clientRepositoryPort.save(existing);
            })
            .flatMap(savedClient -> 
                publishClientEventPort.publish(
                    savedClient.getClientId(),
                    savedClient.getName(),
                    savedClient.getStatus(),
                    "CLIENT_UPDATED"
                )
                .onErrorResume(e -> Mono.empty())
                .thenReturn(savedClient)
            );
    }
}
