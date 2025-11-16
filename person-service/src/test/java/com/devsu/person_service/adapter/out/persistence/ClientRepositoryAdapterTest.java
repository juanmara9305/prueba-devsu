package com.devsu.person_service.adapter.out.persistence;

import com.devsu.person_service.adapter.out.persistence.entity.ClientEntity;
import com.devsu.person_service.adapter.out.persistence.entity.PersonEntity;
import com.devsu.person_service.domain.model.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;

@DataR2dbcTest
@Import(ClientRepositoryAdapter.class)
@org.springframework.test.context.TestPropertySource(locations = "classpath:application-test.properties")
class ClientRepositoryAdapterTest {

    @Autowired
    private ClientRepositoryAdapter clientRepositoryAdapter;

    @Autowired
    private PersonR2dbcRepository personRepository;

    @Autowired
    private ClientR2dbcRepository clientRepository;

    @BeforeEach
    void setUp() {
        clientRepository.deleteAll().block();
        personRepository.deleteAll().block();
    }

    @Test
    void save_withNewClient_shouldCreateBothPersonAndClient() {
        Client client = new Client();
        client.setName("John Doe");
        client.setGender("Male");
        client.setBirthDate(LocalDate.of(1990, 1, 1));
        client.setIdentification("1234567890");
        client.setAddress("123 Main St");
        client.setPhone("555-1234");
        client.setPassword("hashedPassword");
        client.setStatus(true);

        StepVerifier.create(clientRepositoryAdapter.save(client))
                .expectNextMatches(savedClient -> 
                    savedClient.getId() != null &&
                    savedClient.getClientId() != null &&
                    savedClient.getName().equals("John Doe"))
                .verifyComplete();
    }

    @Test
    void save_withExistingClient_shouldUpdateBothTables() {
        PersonEntity person = new PersonEntity();
        person.setName("John Doe");
        person.setGender("Male");
        person.setBirthDate(LocalDate.of(1990, 1, 1));
        person.setIdentification("1234567890");
        person.setAddress("123 Main St");
        person.setPhone("555-1234");
        person.setCreatedAt(LocalDateTime.now());
        person.setUpdatedAt(LocalDateTime.now());
        person.setDeleted(false);

        PersonEntity savedPerson = personRepository.save(person).block();

        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setPersonId(savedPerson.getId());
        clientEntity.setPassword("hashedPassword");
        clientEntity.setStatus(true);
        clientEntity.setCreatedAt(LocalDateTime.now());
        clientEntity.setUpdatedAt(LocalDateTime.now());
        clientEntity.setDeleted(false);

        ClientEntity savedClientEntity = clientRepository.save(clientEntity).block();

        Client client = new Client();
        client.setId(savedPerson.getId());
        client.setClientId(String.valueOf(savedClientEntity.getId()));
        client.setName("John Updated");
        client.setGender("Male");
        client.setBirthDate(LocalDate.of(1990, 1, 1));
        client.setIdentification("1234567890");
        client.setAddress("456 New St");
        client.setPhone("555-5678");
        client.setPassword("newHashedPassword");
        client.setStatus(false);

        StepVerifier.create(clientRepositoryAdapter.save(client))
                .expectNextMatches(updatedClient -> 
                    updatedClient.getName().equals("John Updated") &&
                    updatedClient.getAddress().equals("456 New St") &&
                    !updatedClient.getStatus())
                .verifyComplete();
    }

    @Test
    void findAll_shouldReturnAllNonDeletedClients() {
        PersonEntity person1 = createPersonEntity("John Doe", "1234567890");
        PersonEntity savedPerson1 = personRepository.save(person1).block();
        ClientEntity client1 = createClientEntity(savedPerson1.getId(), "password1", true, false);
        clientRepository.save(client1).block();

        PersonEntity person2 = createPersonEntity("Jane Smith", "0987654321");
        PersonEntity savedPerson2 = personRepository.save(person2).block();
        ClientEntity client2 = createClientEntity(savedPerson2.getId(), "password2", true, false);
        clientRepository.save(client2).block();

        PersonEntity person3 = createPersonEntity("Deleted User", "1111111111");
        PersonEntity savedPerson3 = personRepository.save(person3).block();
        ClientEntity client3 = createClientEntity(savedPerson3.getId(), "password3", true, true);
        clientRepository.save(client3).block();

        StepVerifier.create(clientRepositoryAdapter.findAll())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void findByClientId_withExistingClient_shouldReturnClient() {
        PersonEntity person = createPersonEntity("John Doe", "1234567890");
        PersonEntity savedPerson = personRepository.save(person).block();
        ClientEntity clientEntity = createClientEntity(savedPerson.getId(), "hashedPassword", true, false);
        ClientEntity savedClient = clientRepository.save(clientEntity).block();

        StepVerifier.create(clientRepositoryAdapter.findByClientId(String.valueOf(savedClient.getId())))
                .expectNextMatches(client -> 
                    client.getName().equals("John Doe") &&
                    client.getClientId().equals(String.valueOf(savedClient.getId())))
                .verifyComplete();
    }

    @Test
    void findByClientId_withDeletedClient_shouldReturnEmpty() {
        PersonEntity person = createPersonEntity("Deleted User", "1234567890");
        PersonEntity savedPerson = personRepository.save(person).block();
        ClientEntity clientEntity = createClientEntity(savedPerson.getId(), "hashedPassword", true, true);
        ClientEntity savedClient = clientRepository.save(clientEntity).block();

        StepVerifier.create(clientRepositoryAdapter.findByClientId(String.valueOf(savedClient.getId())))
                .verifyComplete();
    }

    @Test
    void existsByClientId_withExistingClient_shouldReturnTrue() {
        PersonEntity person = createPersonEntity("John Doe", "1234567890");
        PersonEntity savedPerson = personRepository.save(person).block();
        ClientEntity clientEntity = createClientEntity(savedPerson.getId(), "hashedPassword", true, false);
        ClientEntity savedClient = clientRepository.save(clientEntity).block();

        StepVerifier.create(clientRepositoryAdapter.existsByClientId(String.valueOf(savedClient.getId())))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsByClientId_withNonExistentClient_shouldReturnFalse() {
        StepVerifier.create(clientRepositoryAdapter.existsByClientId("999"))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void deleteByClientId_shouldDeleteBothPersonAndClient() {
        PersonEntity person = createPersonEntity("John Doe", "1234567890");
        PersonEntity savedPerson = personRepository.save(person).block();
        ClientEntity clientEntity = createClientEntity(savedPerson.getId(), "hashedPassword", true, false);
        ClientEntity savedClient = clientRepository.save(clientEntity).block();

        StepVerifier.create(clientRepositoryAdapter.deleteByClientId(String.valueOf(savedClient.getId())))
                .verifyComplete();

        StepVerifier.create(personRepository.findById(savedPerson.getId()))
                .verifyComplete();

        StepVerifier.create(clientRepository.findById(savedClient.getId()))
                .verifyComplete();
    }

    private PersonEntity createPersonEntity(String name, String identification) {
        PersonEntity person = new PersonEntity();
        person.setName(name);
        person.setGender("Male");
        person.setBirthDate(LocalDate.of(1990, 1, 1));
        person.setIdentification(identification);
        person.setAddress("123 Main St");
        person.setPhone("555-1234");
        person.setCreatedAt(LocalDateTime.now());
        person.setUpdatedAt(LocalDateTime.now());
        person.setDeleted(false);
        return person;
    }

    private ClientEntity createClientEntity(Long personId, String password, Boolean status, Boolean deleted) {
        ClientEntity client = new ClientEntity();
        client.setPersonId(personId);
        client.setPassword(password);
        client.setStatus(status);
        client.setCreatedAt(LocalDateTime.now());
        client.setUpdatedAt(LocalDateTime.now());
        client.setDeleted(deleted);
        return client;
    }
}
