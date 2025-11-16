package com.devsu.account_service.adapter.in.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientUpdatedEvent {
    private String clientId;
    private String clientName;
    private Boolean clientStatus;
    private String eventType;
    private LocalDateTime timestamp;
}
