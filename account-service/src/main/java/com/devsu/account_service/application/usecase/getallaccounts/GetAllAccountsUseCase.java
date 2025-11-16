package com.devsu.account_service.application.usecase.getallaccounts;

import com.devsu.account_service.domain.model.Account;
import com.devsu.account_service.domain.port.in.GetAllAccountsPort;
import com.devsu.account_service.domain.port.out.AccountRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class GetAllAccountsUseCase implements GetAllAccountsPort {
    private final AccountRepositoryPort accountRepositoryPort;
    
    @Override
    public Flux<Account> execute(Void input) {
        return accountRepositoryPort.findAll();
    }
}
