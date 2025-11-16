package com.devsu.person_service.domain.port.out;

import reactor.core.publisher.Mono;

public interface PublishClientEventPort {
    Mono<Void> publish(String clientId, String clientName, Boolean clientStatus, String eventType);
}
