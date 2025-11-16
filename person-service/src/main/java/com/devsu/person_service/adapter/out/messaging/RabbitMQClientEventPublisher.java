package com.devsu.person_service.adapter.out.messaging;

import com.devsu.person_service.adapter.out.messaging.dto.ClientUpdatedEvent;
import com.devsu.person_service.domain.port.out.PublishClientEventPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQClientEventPublisher implements PublishClientEventPort {
    
    private final RabbitTemplate rabbitTemplate;
    
    @Value("${rabbitmq.exchange.client}")
    private String exchange;
    
    @Value("${rabbitmq.routing-key.client-updated}")
    private String routingKey;
    
    @Override
    public Mono<Void> publish(String clientId, String clientName, Boolean clientStatus, String eventType) {
        return Mono.fromRunnable(() -> {
            ClientUpdatedEvent event = new ClientUpdatedEvent(
                clientId,
                clientName,
                clientStatus,
                eventType,
                LocalDateTime.now()
            );
            
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
            log.info("Published {} event for clientId: {}", eventType, clientId);
        })
        .onErrorResume(e -> {
            log.error("Failed to publish event for clientId: {}, eventType: {}", clientId, eventType, e);
            return Mono.empty();
        })
        .then();
    }
}
