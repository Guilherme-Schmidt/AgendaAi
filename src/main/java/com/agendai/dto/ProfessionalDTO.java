package com.agendai.dto;

import com.agendai.model.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalDTO {

    // Dados do User
    private Long id;
    private String name;
    private String email;
    private UserType type;
    private String phone;
    private String cpf;
    private LocalDate birthDate;
    private Boolean active;
    private LocalDateTime createdAt;
    private String provider;
    private String providerId;
    private String avatar;

    // Dados específicos do Professional
    private Long professionalId;
    private Long establishmentId;
    private String bio;
    private LocalDateTime professionalCreatedAt;

    public static ProfessionalDTO fromEntities(com.agendai.model.User user, com.agendai.model.Professional professional) {
        return ProfessionalDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .type(user.getType())
                .phone(user.getPhone())
                .cpf(user.getCpf())
                .birthDate(user.getBirthDate())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .avatar(user.getAvatar())
                .professionalId(professional.getId())
                .establishmentId(professional.getEstablishmentId())
                .bio(professional.getBio())
                .professionalCreatedAt(professional.getCreatedAt())
                .build();
    }
}