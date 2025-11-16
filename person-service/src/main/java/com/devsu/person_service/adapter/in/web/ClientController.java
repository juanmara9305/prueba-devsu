package com.devsu.person_service.adapter.in.web;

import com.devsu.person_service.adapter.in.web.dto.ClientPatchRequest;
import com.devsu.person_service.adapter.in.web.dto.ClientRequest;
import com.devsu.person_service.adapter.in.web.dto.ClientResponse;
import com.devsu.person_service.adapter.in.web.mapper.ClientMapper;
import com.devsu.person_service.application.usecase.createclient.CreateClientCommand;
import com.devsu.person_service.application.usecase.patchclient.PatchClientCommand;
import com.devsu.person_service.application.usecase.updateclient.UpdateClientCommand;
import com.devsu.person_service.domain.model.Client;
import com.devsu.person_service.domain.port.in.CreateClientPort;
import com.devsu.person_service.domain.port.in.DeleteClientPort;
import com.devsu.person_service.domain.port.in.GetAllClientsPort;
import com.devsu.person_service.domain.port.in.GetClientByIdPort;
import com.devsu.person_service.domain.port.in.PatchClientPort;
import com.devsu.person_service.domain.port.in.UpdateClientPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClientController {
    
    private final CreateClientPort createClientPort;
    private final GetAllClientsPort getAllClientsPort;
    private final GetClientByIdPort getClientByIdPort;
    private final UpdateClientPort updateClientPort;
    private final PatchClientPort patchClientPort;
    private final DeleteClientPort deleteClientPort;
    private final ClientMapper mapper;
    
    @PostMapping
    public Mono<ResponseEntity<ClientResponse>> createClient(@Valid @RequestBody ClientRequest request) {
        Client client = mapper.toDomain(request);
        CreateClientCommand command = new CreateClientCommand(client, request.getPassword());
        
        return createClientPort.execute(command)
            .map(mapper::toResponse)
            .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }
    
    @GetMapping
    public Flux<ClientResponse> getAllClients() {
        return getAllClientsPort.execute(null)
            .map(mapper::toResponse);
    }
    
    @GetMapping("/{clientId}")
    public Mono<ResponseEntity<ClientResponse>> getClientById(@PathVariable String clientId) {
        return getClientByIdPort.execute(clientId)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok);
    }
    
    @PutMapping("/{clientId}")
    public Mono<ResponseEntity<ClientResponse>> updateClient(
            @PathVariable String clientId, 
            @Valid @RequestBody ClientRequest request) {
        Client client = mapper.toDomain(request);
        client.setClientId(clientId);
        UpdateClientCommand command = new UpdateClientCommand(clientId, client, request.getPassword());
        
        return updateClientPort.execute(command)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok);
    }
    
    @PatchMapping("/{clientId}")
    public Mono<ResponseEntity<ClientResponse>> patchClient(
            @PathVariable String clientId, 
            @RequestBody ClientPatchRequest request) {
        Client clientPartial = mapper.toDomainPartial(request);
        PatchClientCommand command = new PatchClientCommand(
            clientId, 
            clientPartial, 
            request.getPassword()
        );
        
        return patchClientPort.execute(command)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok);
    }
    
    @DeleteMapping("/{clientId}")
    public Mono<ResponseEntity<Void>> deleteClient(@PathVariable String clientId) {
        return deleteClientPort.execute(clientId)
            .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}
