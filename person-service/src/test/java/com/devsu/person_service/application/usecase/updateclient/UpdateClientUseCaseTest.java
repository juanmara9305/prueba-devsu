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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

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

    @Test
    void execute_shouldPublishEventAfterSuccessfulUpdate() {
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

        verify(publishClientEventPort, times(1)).publish(
                eq("CLI001"),
                eq("John Updated"),
                eq(true),
                eq("CLIENT_UPDATED")
        );
    }

    @Test
    void execute_shouldSucceedEvenIfEventPublishingFails() {
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
        when(publishClientEventPort.publish(anyString(), anyString(), anyBoolean(), anyString()))
                .thenReturn(Mono.error(new RuntimeException("RabbitMQ connection failed")));

        StepVerifier.create(updateClientUseCase.execute(command))
                .expectNextMatches(result -> result.getClientId().equals("CLI001"))
                .verifyComplete();

        verify(publishClientEventPort, times(1)).publish(
                eq("CLI001"),
                eq("John Updated"),
                eq(true),
                eq("CLIENT_UPDATED")
        );
    }

    @Test
    void execute_shouldPublishEventWithCorrectClientData() {
        Client existingClient = new Client();
        existingClient.setId(1L);
        existingClient.setClientId("CLI002");
        existingClient.setPassword("oldHashedPassword");

        Client updatedClient = new Client();
        updatedClient.setClientId("CLI002");
        updatedClient.setName("Jane Smith");
        updatedClient.setGender("Female");
        updatedClient.setBirthDate(LocalDate.of(1985, 5, 15));
        updatedClient.setIdentification("9876543210");
        updatedClient.setAddress("789 Oak Ave");
        updatedClient.setPhone("555-9999");
        updatedClient.setStatus(false);

        UpdateClientCommand command = new UpdateClientCommand("CLI002", updatedClient, "SecurePass456");

        when(clientRepositoryPort.findByClientId("CLI002")).thenReturn(Mono.just(existingClient));
        when(passwordHasher.hash("SecurePass456")).thenReturn("newHashedPassword");
        when(clientRepositoryPort.save(any(Client.class))).thenReturn(Mono.just(updatedClient));
        when(publishClientEventPort.publish(anyString(), anyString(), anyBoolean(), anyString())).thenReturn(Mono.empty());

        StepVerifier.create(updateClientUseCase.execute(command))
                .expectNextMatches(result -> 
                        result.getClientId().equals("CLI002") &&
                        result.getName().equals("Jane Smith") &&
                        result.getStatus().equals(false))
                .verifyComplete();

        verify(publishClientEventPort, times(1)).publish(
                eq("CLI002"),
                eq("Jane Smith"),
                eq(false),
                eq("CLIENT_UPDATED")
        );
    }
}
