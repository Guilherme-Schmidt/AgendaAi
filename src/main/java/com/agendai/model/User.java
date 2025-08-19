package com.agendai.model;
import com.agendai.model.base.BaseEntity;
import com.agendai.model.enums.UserType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, of = "email")
public class User extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType type;

    private String phone;
    private String cpf;
    private LocalDate birthDate;

    @Builder.Default
    private Boolean active = true;

    // OAuth fields
    private String provider;
    private String providerId;
    private String avatar;

    // Relationships
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Professional professional;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Booking> customerBookings;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<Establishment> ownedEstablishments;

    public User(String email, String password, String name, com.agendai.model.UserType userType) {
        super();
    }
}}