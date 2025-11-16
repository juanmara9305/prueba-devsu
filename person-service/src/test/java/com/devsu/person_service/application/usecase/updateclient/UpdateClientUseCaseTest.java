package com.devsu.person_service.application.usecase.updateclient;

import com.devsu.person_service.application.service.PasswordHasher;
import com.devsu.person_service.domain.exception.ClientNotFoundException;
import com.devsu.person_service.domain.exception.InvalidPasswordException;
import com.devsu.person_service.domain.model.Client;
import com.devsu.person_service.domain.port.out.ClientRepositoryPort;
import com.devsu.person_service.domain.port.out.PublishClientEventPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateClientUseCaseTest {

    @Mock
    private ClientRepositoryPort clientRepositoryPort;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private PublishClientEventPort publishClientEventPort;

    @InjectMocks
    private UpdateClientUseCase updateClientUseCase;

    @Test
    void execute_withValidData_shouldUpdateClient() {
        Client existingClient = new Client();
        existingClient.setId(1L);
        existingClient.setClientId("CLI001");
        existingClient.setPassword("oldHashedPassword");

        Client updatedClient = new Client();
        updatedClient.setClientId("CLI001");
        updatedClient.setName("John Updated");
        updatedClient.setGender("Male");
        updatedClient.setBirthDate(LocalDate.of(1990, 1, 1));
        updatedClient.setIdentification("1234567890");
        updatedClient.setAddress("456 New St");
        updatedClient.setPhone("555-5678");
        updatedClient.setStatus(true);

        UpdateClientCommand command = new UpdateClientCommand("CLI001", updatedClient, "NewPassword123");

        when(clientRepositoryPort.findByClientId("CLI001")).thenReturn(Mono.just(existingClient));
        when(passwordHasher.hash("NewPassword123")).thenReturn("newHashedPassword");
        when(clientRepositoryPort.save(any(Client.class))).thenReturn(Mono.just(updatedClient));
        when(publishClientEventPort.publish(anyString(), anyString(), anyBoolean(), anyString())).thenReturn(Mono.empty());

        StepVerifier.create(updateClientUseCase.execute(command))
                .expectNextMatches(result -> result.getClientId().equals("CLI001"))
                .verifyComplete();
    }

    @Test
    void execute_withNonExistentClient_shouldThrowException() {
        Client client = new Client();
        client.setClientId("INVALID");

        UpdateClientCommand command = new UpdateClientCommand("INVALID", client, "Password123");

        when(clientRepositoryPort.findByClientId("INVALID")).thenReturn(Mono.empty());

        StepVerifier.create(updateClientUseCase.execute(command))
                .expectError(ClientNotFoundException.class)
                .verify();
    }

    @Test
    void execute_withInvalidPassword_shouldThrowException() {
        Client existingClient = new Client();
        existingClient.setClientId("CLI001");

        Client updatedClient = new Client();
        updatedClient.setClientId("CLI001");

        UpdateClientCommand command = new UpdateClientCommand("CLI001", updatedClient, "weak");

        when(clientRepositoryPort.findByClientId("CLI001")).thenReturn(Mono.just(existingClient));

        StepVerifier.create(updateClientUseCase.execute(command))
                .expectError(InvalidPasswordException.class)
                .verify();
    }
}
