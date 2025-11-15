package com.devsu.person_service.application.usecase.createclient;

import com.devsu.person_service.domain.model.Client;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateClientCommand {
    private Client client;
    private String rawPassword;
}
