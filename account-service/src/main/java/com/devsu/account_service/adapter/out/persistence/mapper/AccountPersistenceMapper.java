package com.devsu.account_service.adapter.out.persistence.mapper;

import com.devsu.account_service.adapter.out.persistence.entity.AccountEntity;
import com.devsu.account_service.domain.model.Account;
import com.devsu.account_service.domain.model.AccountType;
import org.springframework.stereotype.Component;

@Component
public class AccountPersistenceMapper {
    
    public AccountEntity toEntity(Account account) {
        if (account == null) {
            return null;
        }
        
        AccountEntity entity = new AccountEntity();
        entity.setId(account.getId());
        entity.setAccountNumber(account.getAccountNumber());
        entity.setAccountType(account.getAccountType() != null ? account.getAccountType().name() : null);
        entity.setBalance(account.getBalance());
        entity.setStatus(account.getStatus());
        entity.setClientId(account.getClientId());
        entity.setClientName(account.getClientName());
        entity.setClientStatus(account.getClientStatus());
        
        return entity;
    }
    
    public Account toDomain(AccountEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Account account = new Account();
        account.setId(entity.getId());
        account.setAccountNumber(entity.getAccountNumber());
        account.setAccountType(entity.getAccountType() != null ? AccountType.valueOf(entity.getAccountType()) : null);
        account.setBalance(entity.getBalance());
        account.setStatus(entity.getStatus());
        account.setClientId(entity.getClientId());
        account.setClientName(entity.getClientName());
        account.setClientStatus(entity.getClientStatus());
        
        return account;
    }
}
