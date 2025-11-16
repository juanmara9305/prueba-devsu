package com.devsu.account_service.application.usecase.updateclientinfo;

import com.devsu.account_service.domain.port.in.UpdateClientInfoPort;
import com.devsu.account_service.domain.port.out.AccountRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateClientInfoUseCase implements UpdateClientInfoPort {
    private final AccountRepositoryPort accountRepositoryPort;
    
    @Override
    public Mono<Void> execute(UpdateClientInfoCommand command) {
        return accountRepositoryPort.findByClientId(command.getClientId())
            .flatMap(account -> {
                account.setClientName(command.getClientName());
                account.setClientStatus(command.getClientStatus());
                return accountRepositoryPort.save(account);
            })
            .then()
            .doOnSuccess(v -> log.info("Updated client info for clientId: {}", command.getClientId()))
            .onErrorResume(e -> {
                log.error("Error updating client info for clientId: {}", command.getClientId(), e);
                return Mono.empty();
            });
    }
}
