package com.devsu.person_service.application.usecase.getclientbyid;

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

import java.time.LocalDate;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetClientByIdUseCaseTest {

    @Mock
    private ClientRepositoryPort clientRepositoryPort;

    @InjectMocks
    private GetClientByIdUseCase getClientByIdUseCase;

    @Test
    void execute_withExistingClient_shouldReturnClient() {
        Client client = new Client();
        client.setClientId("CLI001");
        client.setName("John Doe");
        client.setGender("Male");
        client.setBirthDate(LocalDate.of(1990, 1, 1));

        when(clientRepositoryPort.findByClientId("CLI001")).thenReturn(Mono.just(client));

        StepVerifier.create(getClientByIdUseCase.execute("CLI001"))
                .expectNextMatches(result -> result.getClientId().equals("CLI001"))
                .verifyComplete();
    }

    @Test
    void execute_withNonExistentClient_shouldThrowException() {
        when(clientRepositoryPort.findByClientId("INVALID")).thenReturn(Mono.empty());

        StepVerifier.create(getClientByIdUseCase.execute("INVALID"))
                .expectError(ClientNotFoundException.class)
                .verify();
    }
}
