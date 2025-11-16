package com.devsu.person_service.application.usecase.patchclient;

import com.devsu.person_service.application.service.PasswordHasher;
import com.devsu.person_service.domain.exception.ClientNotFoundException;
import com.devsu.person_service.domain.exception.InvalidPasswordException;
import com.devsu.person_service.domain.model.Client;
import com.devsu.person_service.domain.port.out.ClientRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatchClientUseCaseTest {

    @Mock
    private ClientRepositoryPort clientRepositoryPort;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private PatchClientUseCase patchClientUseCase;

    @Test
    void execute_withPartialData_shouldUpdateOnlyProvidedFields() {
        Client existingClient = new Client();
        existingClient.setId(1L);
        existingClient.setClientId("CLI001");
        existingClient.setName("John Doe");
        existingClient.setGender("Male");
        existingClient.setBirthDate(LocalDate.of(1990, 1, 1));
        existingClient.setIdentification("1234567890");
        existingClient.setAddress("123 Main St");
        existingClient.setPhone("555-1234");
        existingClient.setStatus(true);
        existingClient.setPassword("oldHashedPassword");

        Client partialClient = new Client();
        partialClient.setName("John Updated");
        partialClient.setPhone("555-9999");

        PatchClientCommand command = new PatchClientCommand("CLI001", partialClient, null);

        when(clientRepositoryPort.findByClientId("CLI001")).thenReturn(Mono.just(existingClient));
        when(clientRepositoryPort.save(any(Client.class))).thenReturn(Mono.just(existingClient));

        StepVerifier.create(patchClientUseCase.execute(command))
                .expectNextMatches(result -> 
                    result.getName().equals("John Updated") && 
                    result.getPhone().equals("555-9999") &&
                    result.getGender().equals("Male"))
                .verifyComplete();
    }

    @Test
    void execute_withNonExistentClient_shouldThrowException() {
        Client partialClient = new Client();
        PatchClientCommand command = new PatchClientCommand("INVALID", partialClient, null);

        when(clientRepositoryPort.findByClientId("INVALID")).thenReturn(Mono.empty());

        StepVerifier.create(patchClientUseCase.execute(command))
                .expectError(ClientNotFoundException.class)
                .verify();
    }

    @Test
    void execute_withInvalidPassword_shouldThrowException() {
        Client existingClient = new Client();
        existingClient.setClientId("CLI001");

        Client partialClient = new Client();
        PatchClientCommand command = new PatchClientCommand("CLI001", partialClient, "weak");

        when(clientRepositoryPort.findByClientId("CLI001")).thenReturn(Mono.just(existingClient));

        StepVerifier.create(patchClientUseCase.execute(command))
                .expectError(InvalidPasswordException.class)
                .verify();
    }
}
