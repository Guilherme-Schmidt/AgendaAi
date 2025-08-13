package com.agendai.dto;

import com.agendai.model.User;
import com.agendai.model.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String name;
    private UserType userType;

    // Construtor original (compatibilidade)
    public AuthResponse(String token) {
        this.token = token;
        this.type = "Bearer";
    }

    // Novo construtor para Google Auth
    public AuthResponse(String token, User user) {
        this.token = token;
        this.type = "Bearer";
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.userType = user.getType();
    }
}