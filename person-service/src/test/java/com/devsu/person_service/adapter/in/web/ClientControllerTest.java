package com.devsu.person_service.adapter.in.web;

import com.devsu.person_service.adapter.in.web.dto.ClientPatchRequest;
import com.devsu.person_service.adapter.in.web.dto.ClientRequest;
import com.devsu.person_service.adapter.in.web.dto.ClientResponse;
import com.devsu.person_service.adapter.in.web.mapper.ClientMapper;
import com.devsu.person_service.application.usecase.createclient.CreateClientCommand;
import com.devsu.person_service.application.usecase.patchclient.PatchClientCommand;
import com.devsu.person_service.application.usecase.updateclient.UpdateClientCommand;
import com.devsu.person_service.domain.exception.ClientNotFoundException;
import com.devsu.person_service.domain.exception.InvalidPasswordException;
import com.devsu.person_service.domain.model.Client;
import com.devsu.person_service.domain.port.in.CreateClientPort;
import com.devsu.person_service.domain.port.in.DeleteClientPort;
import com.devsu.person_service.domain.port.in.GetAllClientsPort;
import com.devsu.person_service.domain.port.in.GetClientByIdPort;
import com.devsu.person_service.domain.port.in.PatchClientPort;
import com.devsu.person_service.domain.port.in.UpdateClientPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CreateClientPort createClientPort;

    @MockBean
    private GetAllClientsPort getAllClientsPort;

    @MockBean
    private GetClientByIdPort getClientByIdPort;

    @MockBean
    private UpdateClientPort updateClientPort;

    @MockBean
    private PatchClientPort patchClientPort;

    @MockBean
    private DeleteClientPort deleteClientPort;

    @MockBean
    private ClientMapper mapper;

    @Test
    void createClient_withValidData_shouldReturn201() {
        ClientRequest request = new ClientRequest();
        request.setName("John Doe");
        request.setGender("Male");
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        request.setIdentification("1234567890");
        request.setAddress("123 Main St");
        request.setPhone("555-1234");
        request.setClientId("1");
        request.setPassword("Password123");
        request.setStatus(true);

        Client client = new Client();
        client.setId(1L);
        client.setClientId("1");
        client.setName("John Doe");

        ClientResponse response = new ClientResponse();
        response.setClientId("1");
        response.setName("John Doe");

        when(mapper.toDomain(any(ClientRequest.class))).thenReturn(client);
        when(createClientPort.execute(any(CreateClientCommand.class))).thenReturn(Mono.just(client));
        when(mapper.toResponse(any(Client.class))).thenReturn(response);

        webTestClient.post()
                .uri("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ClientResponse.class);
    }

    @Test
    void createClient_withInvalidPassword_shouldReturn400() {
        ClientRequest request = new ClientRequest();
        request.setName("John Doe");
        request.setGender("Male");
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        request.setIdentification("1234567890");
        request.setAddress("123 Main St");
        request.setPhone("555-1234");
        request.setClientId("1");
        request.setPassword("weak");
        request.setStatus(true);

        Client client = new Client();

        when(mapper.toDomain(any(ClientRequest.class))).thenReturn(client);
        when(createClientPort.execute(any(CreateClientCommand.class)))
                .thenReturn(Mono.error(new InvalidPasswordException("Invalid password")));

        webTestClient.post()
                .uri("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getAllClients_shouldReturn200() {
        Client client1 = new Client();
        client1.setClientId("1");
        client1.setName("John Doe");

        Client client2 = new Client();
        client2.setClientId("2");
        client2.setName("Jane Smith");

        ClientResponse response1 = new ClientResponse();
        response1.setClientId("1");
        response1.setName("John Doe");

        ClientResponse response2 = new ClientResponse();
        response2.setClientId("2");
        response2.setName("Jane Smith");

        when(getAllClientsPort.execute(null)).thenReturn(Flux.just(client1, client2));
        when(mapper.toResponse(client1)).thenReturn(response1);
        when(mapper.toResponse(client2)).thenReturn(response2);

        webTestClient.get()
                .uri("/clientes")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ClientResponse.class)
                .hasSize(2);
    }

    @Test
    void getClientById_withExistingClient_shouldReturn200() {
        Client client = new Client();
        client.setClientId("1");
        client.setName("John Doe");

        ClientResponse response = new ClientResponse();
        response.setClientId("1");
        response.setName("John Doe");

        when(getClientByIdPort.execute("1")).thenReturn(Mono.just(client));
        when(mapper.toResponse(any(Client.class))).thenReturn(response);

        webTestClient.get()
                .uri("/clientes/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ClientResponse.class);
    }

    @Test
    void getClientById_withNonExistentClient_shouldReturn404() {
        when(getClientByIdPort.execute("999"))
                .thenReturn(Mono.error(new ClientNotFoundException("999", "Client not found")));

        webTestClient.get()
                .uri("/clientes/999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateClient_withValidData_shouldReturn200() {
        ClientRequest request = new ClientRequest();
        request.setName("John Updated");
        request.setGender("Male");
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        request.setIdentification("1234567890");
        request.setAddress("456 New St");
        request.setPhone("555-5678");
        request.setClientId("1");
        request.setPassword("NewPassword123");
        request.setStatus(true);

        Client client = new Client();
        client.setClientId("1");
        client.setName("John Updated");

        ClientResponse response = new ClientResponse();
        response.setClientId("1");
        response.setName("John Updated");

        when(mapper.toDomain(any(ClientRequest.class))).thenReturn(client);
        when(updateClientPort.execute(any(UpdateClientCommand.class))).thenReturn(Mono.just(client));
        when(mapper.toResponse(any(Client.class))).thenReturn(response);

        webTestClient.put()
                .uri("/clientes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ClientResponse.class);
    }

    @Test
    void patchClient_withPartialData_shouldReturn200() {
        ClientPatchRequest request = new ClientPatchRequest();
        request.setName("John Patched");
        request.setPhone("555-9999");

        Client partialClient = new Client();
        partialClient.setName("John Patched");

        Client updatedClient = new Client();
        updatedClient.setClientId("1");
        updatedClient.setName("John Patched");

        ClientResponse response = new ClientResponse();
        response.setClientId("1");
        response.setName("John Patched");

        when(mapper.toDomainPartial(any(ClientPatchRequest.class))).thenReturn(partialClient);
        when(patchClientPort.execute(any(PatchClientCommand.class))).thenReturn(Mono.just(updatedClient));
        when(mapper.toResponse(any(Client.class))).thenReturn(response);

        webTestClient.patch()
                .uri("/clientes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ClientResponse.class);
    }

    @Test
    void deleteClient_withExistingClient_shouldReturn204() {
        when(deleteClientPort.execute("1")).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/clientes/1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void deleteClient_withNonExistentClient_shouldReturn404() {
        when(deleteClientPort.execute("999"))
                .thenReturn(Mono.error(new ClientNotFoundException("999", "Client not found")));

        webTestClient.delete()
                .uri("/clientes/999")
                .exchange()
                .expectStatus().isNotFound();
    }
}
