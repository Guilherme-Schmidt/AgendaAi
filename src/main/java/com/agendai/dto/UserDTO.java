package com.agendai.dto;

import com.agendai.model.UserType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDTO {
    private String name;
    private String email;
    private String password;
    private UserType type;
    private String phone;
    private String documentId;
    private LocalDate birthDate;
}
