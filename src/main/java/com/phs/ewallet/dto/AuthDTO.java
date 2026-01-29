package com.phs.ewallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthDTO {
    private String email;
    private String password;
    private String token;
    
    // Constructor for email and password only
    public AuthDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
