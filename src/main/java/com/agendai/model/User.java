package com.agendai.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType type;

    private String phone;

    @Column(name = "cpf")
    private String cpf;

    private LocalDate birthDate;

    private Boolean active = true;

    private LocalDateTime createdAt = LocalDateTime.now();

    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
    }
}
