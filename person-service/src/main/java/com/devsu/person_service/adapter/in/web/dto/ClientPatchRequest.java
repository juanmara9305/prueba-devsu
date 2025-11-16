package com.devsu.person_service.adapter.in.web.dto;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientPatchRequest {
    private String name;
    private String gender;
    
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;
    
    private String identification;
    private String address;
    private String phone;
    
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$", 
             message = "Password must be at least 8 characters with 1 lowercase, 1 uppercase, and 1 digit")
    private String password;
    
    private Boolean status;
}
