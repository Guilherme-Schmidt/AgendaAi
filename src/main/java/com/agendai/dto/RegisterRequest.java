package com.agendai.dto;

import com.agendai.model.UserType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String name;

    @Email(message = "Email deve ser válido")
    @NotBlank(message = "Email é obrigatório")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 50, message = "Senha deve ter entre 6 e 50 caracteres")
    private String password;

    @NotNull(message = "Tipo de usuário é obrigatório")
    private UserType type;

    @Pattern(regexp = "^[0-9\\s\\+\\-\\(\\)]+$", message = "Telefone inválido")
    private String phone;

    private String cpf;

    @Past(message = "Data de nascimento deve ser no passado")
    private LocalDate birthDate;

    // Dados específicos do profissional (opcional)
    @Valid
    private ProfessionalDataRequest professionalData;
}