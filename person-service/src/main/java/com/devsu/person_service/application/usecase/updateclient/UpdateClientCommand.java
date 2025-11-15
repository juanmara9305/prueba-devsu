package com.devsu.person_service.application.usecase.updateclient;

import com.devsu.person_service.domain.model.Client;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateClientCommand {
    private String clientId;
    private Client client;
    private String rawPassword;
}
