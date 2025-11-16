package com.devsu.account_service.application.usecase.patchaccount;

import com.devsu.account_service.domain.model.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatchAccountCommand {
    private String accountNumber;
    private Account accountPartial;
}
