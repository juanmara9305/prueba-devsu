package com.devsu.person_service.application.usecase.patchclient;

import com.devsu.person_service.domain.model.Client;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PatchClientCommand {
    private String clientId;
    private Client clientPartial;
    private String rawPassword;
}
