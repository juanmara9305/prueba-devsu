package com.devsu.account_service.adapter.in.web.mapper;

import com.devsu.account_service.adapter.in.web.dto.TransactionRequest;
import com.devsu.account_service.adapter.in.web.dto.TransactionResponse;
import com.devsu.account_service.adapter.out.persistence.entity.TransactionEntity;
import com.devsu.account_service.domain.model.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    
    public Transaction toDomain(TransactionRequest request) {
        if (request == null) {
            return null;
        }
        
        Transaction transaction = new Transaction();
        transaction.setAccountNumber(request.getAccountNumber());
        transaction.setTransactionType(request.getTransactionType());
        transaction.setAmount(request.getAmount());
        
        return transaction;
    }
    
    public TransactionResponse toResponse(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        
        return new TransactionResponse(
            transaction.getId(),
            transaction.getDate(),
            transaction.getTransactionType(),
            transaction.getAmount(),
            transaction.getBalance(),
            transaction.getAccountNumber()
        );
    }
    
    public TransactionEntity toEntity(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        
        TransactionEntity entity = new TransactionEntity();
        entity.setId(transaction.getId());
        entity.setDate(transaction.getDate());
        entity.setTransactionType(transaction.getTransactionType());
        entity.setAmount(transaction.getAmount());
        entity.setBalance(transaction.getBalance());
        entity.setAccountNumber(transaction.getAccountNumber());
        
        return entity;
    }
    
    public Transaction toDomain(TransactionEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Transaction transaction = new Transaction();
        transaction.setId(entity.getId());
        transaction.setDate(entity.getDate());
        transaction.setTransactionType(entity.getTransactionType());
        transaction.setAmount(entity.getAmount());
        transaction.setBalance(entity.getBalance());
        transaction.setAccountNumber(entity.getAccountNumber());
        
        return transaction;
    }
}
