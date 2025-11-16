package com.devsu.person_service.application.usecase.createclient;

import com.devsu.person_service.application.service.PasswordHasher;
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
class CreateClientUseCaseTest {

    @Mock
    private ClientRepositoryPort clientRepositoryPort;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private CreateClientUseCase createClientUseCase;

    @Test
    void execute_withValidData_shouldCreateClient() {
        Client client = new Client();
        client.setName("John Doe");
        client.setGender("Male");
        client.setBirthDate(LocalDate.of(1990, 1, 1));
        client.setIdentification("1234567890");
        client.setAddress("123 Main St");
        client.setPhone("555-1234");
        client.setStatus(true);

        CreateClientCommand command = new CreateClientCommand(client, "Password123");

        Client savedClient = new Client();
        savedClient.setId(1L);
        savedClient.setClientId("1");
        savedClient.setName("John Doe");
        savedClient.setPassword("hashedPassword");

        when(passwordHasher.hash("Password123")).thenReturn("hashedPassword");
        when(clientRepositoryPort.save(any(Client.class))).thenReturn(Mono.just(savedClient));

        StepVerifier.create(createClientUseCase.execute(command))
                .expectNextMatches(result -> result.getPassword().equals("hashedPassword"))
                .verifyComplete();
    }

    @Test
    void execute_withInvalidPassword_shouldThrowException() {
        Client client = new Client();
        client.setClientId("CLI001");

        CreateClientCommand command = new CreateClientCommand(client, "weak");

        StepVerifier.create(createClientUseCase.execute(command))
                .expectError(InvalidPasswordException.class)
                .verify();
    }
}
