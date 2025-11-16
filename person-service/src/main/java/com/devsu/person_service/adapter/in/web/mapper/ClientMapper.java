package com.devsu.person_service.adapter.in.web.mapper;

import com.devsu.person_service.adapter.in.web.dto.ClientPatchRequest;
import com.devsu.person_service.adapter.in.web.dto.ClientRequest;
import com.devsu.person_service.adapter.in.web.dto.ClientResponse;
import com.devsu.person_service.adapter.out.persistence.entity.ClientEntity;
import com.devsu.person_service.adapter.out.persistence.entity.PersonEntity;
import com.devsu.person_service.domain.model.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {
    
    public Client toDomain(ClientRequest request) {
        Client client = new Client();
        client.setName(request.getName());
        client.setGender(request.getGender());
        client.setBirthDate(request.getBirthDate());
        client.setIdentification(request.getIdentification());
        client.setAddress(request.getAddress());
        client.setPhone(request.getPhone());
        client.setClientId(request.getClientId());
        client.setStatus(request.getStatus());
        return client;
    }
    
    public Client toDomainPartial(ClientPatchRequest request) {
        Client client = new Client();
        client.setName(request.getName());
        client.setGender(request.getGender());
        client.setBirthDate(request.getBirthDate());
        client.setIdentification(request.getIdentification());
        client.setAddress(request.getAddress());
        client.setPhone(request.getPhone());
        client.setStatus(request.getStatus());
        return client;
    }
    
    public ClientResponse toResponse(Client client) {
        ClientResponse response = new ClientResponse();
        response.setClientId(client.getClientId());
        response.setName(client.getName());
        response.setGender(client.getGender());
        response.setBirthDate(client.getBirthDate());
        response.setAge(client.getAge());
        response.setIdentification(client.getIdentification());
        response.setAddress(client.getAddress());
        response.setPhone(client.getPhone());
        response.setStatus(client.getStatus());
        return response;
    }
    
    public PersonEntity toPersonEntity(Client client) {
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
    
    public ClientEntity toClientEntity(Client client) {
        ClientEntity entity = new ClientEntity();
        entity.setPersonId(client.getId());
        entity.setClientId(client.getClientId());
        entity.setPassword(client.getPassword());
        entity.setStatus(client.getStatus());
        return entity;
    }
    
    public Client toDomain(PersonEntity personEntity, ClientEntity clientEntity) {
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
