package com.agendai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.agendai.model.User;
import com.agendai.model.UserType;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    // Métodos para buscar por tipo
    List<User> findByType(UserType type);
    List<User> findByTypeAndActiveTrue(UserType type);

    // Buscar profissionais específicos
    @Query("SELECT u FROM User u WHERE u.type = 'PROFESSIONAL' AND u.active = true")
    List<User> findActiveProfessionals();

    @Query("SELECT u FROM User u WHERE u.type = 'CLIENT' AND u.active = true")
    List<User> findActiveClients();

    // Buscar profissionais por estabelecimento usando o relacionamento correto
    @Query("SELECT u FROM User u JOIN u.professional p WHERE p.establishmentId = :establishmentId AND u.active = true")
    List<User> findProfessionalsByEstablishment(@Param("establishmentId") Long establishmentId);
}