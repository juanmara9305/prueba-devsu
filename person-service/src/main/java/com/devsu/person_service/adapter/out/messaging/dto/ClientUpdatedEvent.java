package com.devsu.person_service.adapter.out.messaging.dto;

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
