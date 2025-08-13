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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType type;

    private String phone;

    @Column(name = "cpf")
    private String cpf;

    private LocalDate birthDate;

    @Builder.Default
    private Boolean active = true;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private String provider;
    private String providerId;
    private String avatar;

    // Relacionamento com Professional (para usuários do tipo PROFESSIONAL)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Professional professional;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}