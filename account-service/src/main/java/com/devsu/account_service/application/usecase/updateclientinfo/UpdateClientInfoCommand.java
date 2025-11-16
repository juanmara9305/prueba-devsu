package com.devsu.account_service.application.usecase.updateclientinfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClientInfoCommand {
    private String clientId;
    private String clientName;
    private Boolean clientStatus;
}
