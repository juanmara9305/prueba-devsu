package com.devsu.account_service.application.usecase.createaccount;

import com.devsu.account_service.domain.exception.AccountAlreadyExistsException;
import com.devsu.account_service.domain.exception.InactiveClientException;
import com.devsu.account_service.domain.model.Account;
import com.devsu.account_service.domain.port.in.CreateAccountPort;
import com.devsu.account_service.domain.port.out.AccountRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CreateAccountUseCase implements CreateAccountPort {
    private final AccountRepositoryPort accountRepositoryPort;
    
    @Override
    public Mono<Account> execute(CreateAccountCommand command) {
        if (!Boolean.TRUE.equals(command.getAccount().getClientStatus())) {
            return Mono.error(new InactiveClientException(
                command.getAccount().getClientId(),
                "Cannot create account for inactive client"));
        }
        
        return accountRepositoryPort.existsByAccountNumber(command.getAccount().getAccountNumber())
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new AccountAlreadyExistsException(
                        command.getAccount().getAccountNumber(),
                        "An account with this number already exists"));
                }
                return accountRepositoryPort.save(command.getAccount());
            });
    }
}
