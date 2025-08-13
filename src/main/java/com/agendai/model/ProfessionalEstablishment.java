package com.agendai.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "professional_establishments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfessionalEstablishment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "professional_id", nullable = false)
    private User professional;

    @ManyToOne
    @JoinColumn(name = "establishment_id", nullable = false)
    private Establishment establishment;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}