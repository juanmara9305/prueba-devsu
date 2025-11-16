package com.devsu.account_service.adapter.in.web;

import com.devsu.account_service.adapter.in.web.dto.TransactionRequest;
import com.devsu.account_service.adapter.in.web.dto.TransactionResponse;
import com.devsu.account_service.adapter.in.web.mapper.TransactionMapper;
import com.devsu.account_service.application.usecase.createtransaction.CreateTransactionCommand;
import com.devsu.account_service.application.usecase.updatetransaction.UpdateTransactionCommand;
import com.devsu.account_service.domain.port.in.CreateTransactionPort;
import com.devsu.account_service.domain.port.in.GetAllTransactionsPort;
import com.devsu.account_service.domain.port.in.GetTransactionByIdPort;
import com.devsu.account_service.domain.port.in.UpdateTransactionPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
public class TransactionController {
    private final CreateTransactionPort createTransactionPort;
    private final GetAllTransactionsPort getAllTransactionsPort;
    private final GetTransactionByIdPort getTransactionByIdPort;
    private final UpdateTransactionPort updateTransactionPort;
    private final TransactionMapper mapper;
    
    @PostMapping
    public Mono<ResponseEntity<TransactionResponse>> createTransaction(
            @Valid @RequestBody TransactionRequest request) {
        CreateTransactionCommand command = new CreateTransactionCommand(
            request.getAccountNumber(),
            request.getTransactionType(),
            request.getAmount()
        );
        
        return createTransactionPort.execute(command)
            .map(mapper::toResponse)
            .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }
    
    @GetMapping
    public Flux<TransactionResponse> getAllTransactions() {
        return getAllTransactionsPort.execute(null)
            .map(mapper::toResponse);
    }
    
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TransactionResponse>> getTransactionById(@PathVariable Long id) {
        return getTransactionByIdPort.execute(id)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok);
    }
    
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TransactionResponse>> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request) {
        UpdateTransactionCommand command = new UpdateTransactionCommand(
            id,
            request.getTransactionType(),
            request.getAmount()
        );
        
        return updateTransactionPort.execute(command)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok);
    }
}
