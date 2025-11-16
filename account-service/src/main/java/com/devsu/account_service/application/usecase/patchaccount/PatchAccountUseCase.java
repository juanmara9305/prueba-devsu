package com.devsu.account_service.application.usecase.patchaccount;

import com.devsu.account_service.domain.exception.AccountNotFoundException;
import com.devsu.account_service.domain.model.Account;
import com.devsu.account_service.domain.port.in.PatchAccountPort;
import com.devsu.account_service.domain.port.out.AccountRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PatchAccountUseCase implements PatchAccountPort {
    private final AccountRepositoryPort accountRepositoryPort;
    
    @Override
    public Mono<Account> execute(PatchAccountCommand command) {
        return accountRepositoryPort.findByAccountNumber(command.getAccountNumber())
            .switchIfEmpty(Mono.error(new AccountNotFoundException(
                command.getAccountNumber(),
                "Account not found")))
            .flatMap(existingAccount -> {
                if (command.getAccountPartial().getAccountType() != null) {
                    existingAccount.setAccountType(command.getAccountPartial().getAccountType());
                }
                if (command.getAccountPartial().getStatus() != null) {
                    existingAccount.setStatus(command.getAccountPartial().getStatus());
                }
                return accountRepositoryPort.save(existingAccount);
            });
    }
}
