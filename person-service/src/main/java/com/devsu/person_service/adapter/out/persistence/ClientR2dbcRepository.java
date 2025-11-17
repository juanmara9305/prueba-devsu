package com.devsu.person_service.adapter.out.persistence;

import com.devsu.person_service.adapter.out.persistence.entity.ClientEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientR2dbcRepository extends ReactiveCrudRepository<ClientEntity, Long> {
    // Standard CRUD operations provided by ReactiveCrudRepository
}
