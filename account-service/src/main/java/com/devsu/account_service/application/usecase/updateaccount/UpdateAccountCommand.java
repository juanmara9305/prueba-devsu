package com.devsu.account_service.application.usecase.updateaccount;

import com.devsu.account_service.domain.model.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAccountCommand {
    private String accountNumber;
    private Account account;
}
