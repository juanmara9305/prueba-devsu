package com.devsu.person_service.domain.port.in;

import com.devsu.person_service.domain.usecase.UseCase;
import reactor.core.publisher.Mono;

public interface DeleteClientPort extends UseCase<String, Mono<Void>> {
}
