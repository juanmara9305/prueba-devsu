package com.devsu.person_service.adapter.out.persistence;

import com.devsu.person_service.adapter.out.persistence.entity.PersonEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PersonR2dbcRepository extends ReactiveCrudRepository<PersonEntity, Long> {
    Mono<PersonEntity> findByIdentification(String identification);
}
