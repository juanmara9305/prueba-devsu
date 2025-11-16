package com.devsu.person_service.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table("person")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonEntity {
    @Id
    private Long id;
    
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String identification;
    private String address;
    private String phone;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private LocalDateTime deletedAt;
}
