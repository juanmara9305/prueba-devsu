package com.devsu.account_service.adapter.in.messaging;

import com.devsu.account_service.adapter.in.messaging.dto.ClientUpdatedEvent;
import com.devsu.account_service.application.usecase.updateclientinfo.UpdateClientInfoCommand;
import com.devsu.account_service.domain.port.in.UpdateClientInfoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientUpdateListener {
    private final UpdateClientInfoPort updateClientInfoPort;
    
    @RabbitListener(queues = "${rabbitmq.queue.client-updated}")
    public void handleClientUpdated(ClientUpdatedEvent event) {
        log.info("Received client updated event for clientId: {}", event.getClientId());
        
        UpdateClientInfoCommand command = new UpdateClientInfoCommand(
            event.getClientId(),
            event.getClientName(),
            event.getClientStatus()
        );
        
        updateClientInfoPort.execute(command)
            .doOnSuccess(v -> log.info("Successfully updated client info for clientId: {}", event.getClientId()))
            .doOnError(e -> log.error("Failed to update client info for clientId: {}", event.getClientId(), e))
            .subscribe();
    }
}
