package com.devsu.person_service.application.usecase.deleteclient;

import com.devsu.person_service.domain.exception.ClientNotFoundException;
import com.devsu.person_service.domain.model.Client;
import com.devsu.person_service.domain.port.out.ClientRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteClientUseCaseTest {

    @Mock
    private ClientRepositoryPort clientRepositoryPort;

    @InjectMocks
    private DeleteClientUseCase deleteClientUseCase;

    @Test
    void execute_withExistingClient_shouldDeleteClient() {
        Client client = new Client();
        client.setClientId("CLI001");

        when(clientRepositoryPort.findByClientId("CLI001")).thenReturn(Mono.just(client));
        when(clientRepositoryPort.deleteByClientId("CLI001")).thenReturn(Mono.empty());

        StepVerifier.create(deleteClientUseCase.execute("CLI001"))
                .verifyComplete();
    }

    @Test
    void execute_withNonExistentClient_shouldThrowException() {
        when(clientRepositoryPort.findByClientId("INVALID")).thenReturn(Mono.empty());

        StepVerifier.create(deleteClientUseCase.execute("INVALID"))
                .expectError(ClientNotFoundException.class)
                .verify();
    }
}
