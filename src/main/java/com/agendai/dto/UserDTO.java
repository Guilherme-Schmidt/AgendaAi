package com.agendai.dto;

import com.agendai.model.UserType;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Data
public class UserDTO {
    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @Email(message = "Email deve ser válido")
    @NotBlank(message = "Email é obrigatório")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    private String password;

    @NotNull(message = "Tipo de usuário é obrigatório")
    private UserType type;

    private String phone;
    private String documentId; // CPF
    private LocalDate birthDate;

    // Campos específicos para PROFESSIONAL
    private Long establishmentId;
    private String bio;
}