package com.agendai.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "professionals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Professional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    // establishment_id é OPCIONAL - pode ser null inicialmente
    @Column(name = "establishment_id", nullable = true)
    private Long establishmentId;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Builder.Default
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Relacionamento com User
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    // Relacionamento com Establishment (opcional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "establishment_id", insertable = false, updatable = false)
    private Establishment establishment;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // Métodos de conveniência
    public boolean hasEstablishment() {
        return establishmentId != null;
    }

    public boolean isAssignedToEstablishment(Long establishmentId) {
        return this.establishmentId != null && this.establishmentId.equals(establishmentId);
    }
}