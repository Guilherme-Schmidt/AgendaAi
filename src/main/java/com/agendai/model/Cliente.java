package com.agendai.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "clientes")
@DiscriminatorValue("CLIENTE")
@Data
@AllArgsConstructor
public class Cliente extends User {

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "cpf")
    private String cpf;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "zipcode")
    private String zipcode;

    @Column(name = "preferences", columnDefinition = "TEXT")
    private String preferences; // JSON string com preferências

    // Relacionamentos
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> agendamentos;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Avaliacao> avaliacoes;

    public Cliente() {
        super();
        setRole(UserType.CLIENT);
    }

    public Cliente(String email, String password, String name) {
        super(email, password, name, UserType.CLIENT);
    }