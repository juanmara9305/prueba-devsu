package com.devsu.person_service.application.usecase.getallclients;

import com.devsu.person_service.domain.model.Client;
import com.devsu.person_service.domain.port.out.ClientRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllClientsUseCaseTest {

    @Mock
    private ClientRepositoryPort clientRepositoryPort;

    @InjectMocks
    private GetAllClientsUseCase getAllClientsUseCase;

    @Test
    void execute_shouldReturnAllClients() {
        Client client1 = new Client();
        client1.setClientId("CLI001");
        client1.setName("John Doe");
        client1.setGender("Male");
        client1.setBirthDate(LocalDate.of(1990, 1, 1));

        Client client2 = new Client();
        client2.setClientId("CLI002");
        client2.setName("Jane Smith");
        client2.setGender("Female");
        client2.setBirthDate(LocalDate.of(1985, 5, 15));

        when(clientRepositoryPort.findAll()).thenReturn(Flux.just(client1, client2));

        StepVerifier.create(getAllClientsUseCase.execute(null))
                .expectNext(client1)
                .expectNext(client2)
                .verifyComplete();
    }

    @Test
    void execute_withNoClients_shouldReturnEmptyFlux() {
        when(clientRepositoryPort.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(getAllClientsUseCase.execute(null))
                .verifyComplete();
    }
}
