package com.devsu.person_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Client extends Person {
    private String clientId;
    private String password;
    private Boolean status;
    
    public void activate() {
        this.status = true;
    }
    
    public void deactivate() {
        this.status = false;
    }
    
    public boolean isActive() {
        return Boolean.TRUE.equals(this.status);
    }
    
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        return hasLower && hasUpper && hasDigit;
    }
}
