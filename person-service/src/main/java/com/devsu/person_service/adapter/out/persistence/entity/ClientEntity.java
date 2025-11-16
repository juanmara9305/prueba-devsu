package com.devsu.person_service.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("client")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientEntity {
    @Id
    private Long id;

    private Long personId;
    private String password;
    private Boolean status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private LocalDateTime deletedAt;
}
