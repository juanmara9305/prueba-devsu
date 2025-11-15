package com.devsu.person_service.adapter.out.persistence;

import com.devsu.person_service.adapter.out.persistence.entity.PersonEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonR2dbcRepository extends ReactiveCrudRepository<PersonEntity, Long> {
}
