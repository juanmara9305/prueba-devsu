package com.devsu.account_service.adapter.out.persistence.mapper;

import com.devsu.account_service.adapter.out.persistence.entity.TransactionEntity;
import com.devsu.account_service.domain.model.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionPersistenceMapper {
    
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
