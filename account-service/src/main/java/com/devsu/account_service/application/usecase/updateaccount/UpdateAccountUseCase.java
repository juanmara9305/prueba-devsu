package com.devsu.account_service.application.usecase.updateaccount;

import com.devsu.account_service.domain.exception.AccountNotFoundException;
import com.devsu.account_service.domain.model.Account;
import com.devsu.account_service.domain.port.in.UpdateAccountPort;
import com.devsu.account_service.domain.port.out.AccountRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UpdateAccountUseCase implements UpdateAccountPort {
    private final AccountRepositoryPort accountRepositoryPort;
    
    @Override
    public Mono<Account> execute(UpdateAccountCommand command) {
        return accountRepositoryPort.findByAccountNumber(command.getAccountNumber())
            .switchIfEmpty(Mono.error(new AccountNotFoundException(
                command.getAccountNumber(),
                "Account not found")))
            .flatMap(existingAccount -> {
                existingAccount.setAccountType(command.getAccount().getAccountType());
                existingAccount.setStatus(command.getAccount().getStatus());
                return accountRepositoryPort.save(existingAccount);
            });
    }
}
