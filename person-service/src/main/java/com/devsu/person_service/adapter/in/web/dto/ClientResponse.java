package com.devsu.person_service.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
    private String clientId;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private Integer age;
    private String identification;
    private String address;
    private String phone;
    private Boolean status;
}
