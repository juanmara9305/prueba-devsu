package com.devsu.account_service.application.usecase.getaccountbynumber;

import com.devsu.account_service.domain.exception.AccountNotFoundException;
import com.devsu.account_service.domain.model.Account;
import com.devsu.account_service.domain.port.in.GetAccountByNumberPort;
import com.devsu.account_service.domain.port.out.AccountRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GetAccountByNumberUseCase implements GetAccountByNumberPort {
    private final AccountRepositoryPort accountRepositoryPort;
    
    @Override
    public Mono<Account> execute(String accountNumber) {
        return accountRepositoryPort.findByAccountNumber(accountNumber)
            .switchIfEmpty(Mono.error(new AccountNotFoundException(
                accountNumber,
                "Account not found")));
    }
}
