package com.devsu.account_service.adapter.in.web;

import com.devsu.account_service.adapter.in.web.dto.AccountPatchRequest;
import com.devsu.account_service.adapter.in.web.dto.AccountRequest;
import com.devsu.account_service.adapter.in.web.dto.AccountResponse;
import com.devsu.account_service.adapter.in.web.mapper.AccountMapper;
import com.devsu.account_service.application.usecase.createaccount.CreateAccountCommand;
import com.devsu.account_service.application.usecase.patchaccount.PatchAccountCommand;
import com.devsu.account_service.application.usecase.updateaccount.UpdateAccountCommand;
import com.devsu.account_service.domain.model.Account;
import com.devsu.account_service.domain.port.in.CreateAccountPort;
import com.devsu.account_service.domain.port.in.GetAccountByNumberPort;
import com.devsu.account_service.domain.port.in.GetAllAccountsPort;
import com.devsu.account_service.domain.port.in.PatchAccountPort;
import com.devsu.account_service.domain.port.in.UpdateAccountPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class AccountController {
    private final CreateAccountPort createAccountPort;
    private final GetAllAccountsPort getAllAccountsPort;
    private final GetAccountByNumberPort getAccountByNumberPort;
    private final UpdateAccountPort updateAccountPort;
    private final PatchAccountPort patchAccountPort;
    private final AccountMapper mapper;
    
    @PostMapping
    public Mono<ResponseEntity<AccountResponse>> createAccount(@Valid @RequestBody AccountRequest request) {
        Account account = mapper.toDomain(request);
        CreateAccountCommand command = new CreateAccountCommand(account);
        
        return createAccountPort.execute(command)
            .map(mapper::toResponse)
            .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }
    
    @GetMapping
    public Flux<AccountResponse> getAllAccounts() {
        return getAllAccountsPort.execute(null)
            .map(mapper::toResponse);
    }
    
    @GetMapping("/{accountNumber}")
    public Mono<ResponseEntity<AccountResponse>> getAccountByNumber(@PathVariable String accountNumber) {
        return getAccountByNumberPort.execute(accountNumber)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok);
    }
    
    @PutMapping("/{accountNumber}")
    public Mono<ResponseEntity<AccountResponse>> updateAccount(
            @PathVariable String accountNumber,
            @Valid @RequestBody AccountRequest request) {
        Account account = mapper.toDomain(request);
        account.setAccountNumber(accountNumber);
        UpdateAccountCommand command = new UpdateAccountCommand(accountNumber, account);
        
        return updateAccountPort.execute(command)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok);
    }
    
    @PatchMapping("/{accountNumber}")
    public Mono<ResponseEntity<AccountResponse>> patchAccount(
            @PathVariable String accountNumber,
            @RequestBody AccountPatchRequest request) {
        Account accountPartial = mapper.toDomainPartial(request);
        PatchAccountCommand command = new PatchAccountCommand(accountNumber, accountPartial);
        
        return patchAccountPort.execute(command)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok);
    }
}
