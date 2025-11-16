package com.devsu.person_service.adapter.out.persistence;

import com.devsu.person_service.adapter.out.persistence.entity.ClientEntity;
import com.devsu.person_service.adapter.out.persistence.entity.PersonEntity;
import com.devsu.person_service.domain.model.Client;
import com.devsu.person_service.domain.port.out.ClientRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ClientRepositoryAdapter implements ClientRepositoryPort {
    private final PersonR2dbcRepository personRepository;
    private final ClientR2dbcRepository clientRepository;
    
    @Override
    @Transactional
    public Mono<Client> save(Client client) {
        PersonEntity personEntity = toPersonEntity(client);
        LocalDateTime now = LocalDateTime.now();
        
        if (client.getId() == null) {
            personEntity.setCreatedAt(now);
            personEntity.setUpdatedAt(now);
            personEntity.setDeleted(false);
            
            return personRepository.save(personEntity)
                .flatMap(savedPerson -> {
                    ClientEntity clientEntity = new ClientEntity();
                    clientEntity.setPersonId(savedPerson.getId());
                    clientEntity.setPassword(client.getPassword());
                    clientEntity.setStatus(client.getStatus());
                    clientEntity.setCreatedAt(now);
                    clientEntity.setUpdatedAt(now);
                    clientEntity.setDeleted(false);
                    
                    return clientRepository.save(clientEntity)
                        .map(savedClient -> toDomain(savedPerson, savedClient));
                });
        } else {
            return personRepository.findById(client.getId())
                .flatMap(existing -> {
                    personEntity.setId(existing.getId());
                    personEntity.setCreatedAt(existing.getCreatedAt());
                    personEntity.setUpdatedAt(now);
                    personEntity.setDeleted(false);
                    personEntity.setDeletedAt(null);
                    
                    return personRepository.save(personEntity)
                        .flatMap(savedPerson -> {
                            return clientRepository.findById(Long.parseLong(client.getClientId()))
                                .flatMap(existingClient -> {
                                    existingClient.setPassword(client.getPassword());
                                    existingClient.setStatus(client.getStatus());
                                    existingClient.setUpdatedAt(now);
                                    existingClient.setDeleted(false);
                                    existingClient.setDeletedAt(null);
                                    
                                    return clientRepository.save(existingClient);
                                })
                                .map(savedClient -> toDomain(savedPerson, savedClient));
                        });
                });
        }
    }
    
    @Override
    public Flux<Client> findAll() {
        return clientRepository.findAll()
            .filter(clientEntity -> !Boolean.TRUE.equals(clientEntity.getDeleted()))
            .flatMap(clientEntity -> 
                personRepository.findById(clientEntity.getPersonId())
                    .filter(personEntity -> !Boolean.TRUE.equals(personEntity.getDeleted()))
                    .map(personEntity -> toDomain(personEntity, clientEntity))
            );
    }
    
    @Override
    public Mono<Client> findByClientId(String clientId) {
        return clientRepository.findById(Long.parseLong(clientId))
            .filter(clientEntity -> !Boolean.TRUE.equals(clientEntity.getDeleted()))
            .flatMap(clientEntity -> 
                personRepository.findById(clientEntity.getPersonId())
                    .filter(personEntity -> !Boolean.TRUE.equals(personEntity.getDeleted()))
                    .map(personEntity -> toDomain(personEntity, clientEntity))
            );
    }
    
    @Override
    public Mono<Boolean> existsByClientId(String clientId) {
        return clientRepository.findById(Long.parseLong(clientId))
            .filter(clientEntity -> !Boolean.TRUE.equals(clientEntity.getDeleted()))
            .hasElement();
    }
    
    @Override
    @Transactional
    public Mono<Void> deleteByClientId(String clientId) {
        return clientRepository.findById(Long.parseLong(clientId))
            .filter(clientEntity -> !Boolean.TRUE.equals(clientEntity.getDeleted()))
            .flatMap(clientEntity -> 
                clientRepository.deleteById(Long.parseLong(clientId))
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
        client.setClientId(String.valueOf(clientEntity.getId()));
        client.setPassword(clientEntity.getPassword());
        client.setStatus(clientEntity.getStatus());
        return client;
    }
}
