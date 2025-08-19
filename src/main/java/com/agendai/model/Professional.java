package com.agendai.model;

import com.agendai.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "professionals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Professional extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "establishment_id")
    private Establishment establishment;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "is_available")
    @Builder.Default
    private Boolean available = true;

    // Relationships
    @OneToMany(mappedBy = "professional", fetch = FetchType.LAZY)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "professional", fetch = FetchType.LAZY)
    private List<Review> reviews;

    // Business methods
    public boolean hasEstablishment() {
        return establishment != null;
    }

    public String getName() {
        return user != null ? user.getName() : null;
    }

    public String getEmail() {
        return user != null ? user.getEmail() : null;
    }
}}