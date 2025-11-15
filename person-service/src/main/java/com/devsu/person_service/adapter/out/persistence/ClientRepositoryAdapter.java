package com.devsu.person_service.adapter.out.persistence;

import com.devsu.person_service.adapter.out.persistence.entity.ClientEntity;
import com.devsu.person_service.adapter.out.persistence.entity.PersonEntity;
import com.devsu.person_service.domain.model.Client;
import com.devsu.person_service.domain.port.out.ClientRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ClientRepositoryAdapter implements ClientRepositoryPort {
    private final PersonR2dbcRepository personRepository;
    private final ClientR2dbcRepository clientRepository;
    
    @Override
    public Mono<Client> save(Client client) {
        PersonEntity personEntity = toPersonEntity(client);
        
        return (client.getId() == null ? 
                personRepository.save(personEntity) : 
                personRepository.findById(client.getId())
                    .flatMap(existing -> {
                        personEntity.setId(existing.getId());
                        personEntity.setCreatedAt(existing.getCreatedAt());
                        personEntity.setUpdatedAt(LocalDateTime.now());
                        return personRepository.save(personEntity);
                    }))
            .flatMap(savedPerson -> {
                ClientEntity clientEntity = toClientEntity(client);
                clientEntity.setPersonId(savedPerson.getId());
                
                if (client.getId() == null) {
                    clientEntity.setCreatedAt(LocalDateTime.now());
                }
                clientEntity.setUpdatedAt(LocalDateTime.now());
                
                return clientRepository.save(clientEntity)
                    .map(savedClient -> toDomain(savedPerson, savedClient));
            });
    }
    
    @Override
    public Flux<Client> findAll() {
        return clientRepository.findAll()
            .flatMap(clientEntity -> 
                personRepository.findById(clientEntity.getPersonId())
                    .map(personEntity -> toDomain(personEntity, clientEntity))
            );
    }
    
    @Override
    public Mono<Client> findByClientId(String clientId) {
        return clientRepository.findByClientId(clientId)
            .flatMap(clientEntity -> 
                personRepository.findById(clientEntity.getPersonId())
                    .map(personEntity -> toDomain(personEntity, clientEntity))
            );
    }
    
    @Override
    public Mono<Boolean> existsByClientId(String clientId) {
        return clientRepository.existsByClientId(clientId);
    }
    
    @Override
    public Mono<Void> deleteByClientId(String clientId) {
        return clientRepository.findByClientId(clientId)
            .flatMap(clientEntity -> 
                clientRepository.deleteByClientId(clientId)
                    .then(personRepository.deleteById(clientEntity.getPersonId()))
            );
    }
    
    private PersonEntity toPersonEntity(Client client) {
        PersonEntity entity = new PersonEntity();
        entity.setId(client.getId());
        entity.setName(client.getName());
        entity.setGender(client.getGender());
        entity.setBirthDate(client.getBirthDate());
        entity.setIdentification(client.getIdentification());
        entity.setAddress(client.getAddress());
        entity.setPhone(client.getPhone());
        return entity;
    }
    
    private ClientEntity toClientEntity(Client client) {
        ClientEntity entity = new ClientEntity();
        entity.setPersonId(client.getId());
        entity.setClientId(client.getClientId());
        entity.setPassword(client.getPassword());
        entity.setStatus(client.getStatus());
        return entity;
    }
    
    private Client toDomain(PersonEntity personEntity, ClientEntity clientEntity) {
        Client client = new Client();
        client.setId(personEntity.getId());
        client.setName(personEntity.getName());
        client.setGender(personEntity.getGender());
        client.setBirthDate(personEntity.getBirthDate());
        client.setIdentification(personEntity.getIdentification());
        client.setAddress(personEntity.getAddress());
        client.setPhone(personEntity.getPhone());
        client.setClientId(clientEntity.getClientId());
        client.setPassword(clientEntity.getPassword());
        client.setStatus(clientEntity.getStatus());
        return client;
    }
}
