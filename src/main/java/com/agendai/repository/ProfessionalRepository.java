package com.agendai.repository;

import com.agendai.model.Professional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProfessionalRepository extends JpaRepository<Professional, Long> {

    Optional<Professional> findByUserId(Long userId);

    List<Professional> findByEstablishmentId(Long establishmentId);

    boolean existsByUserId(Long userId);

    @Query("SELECT p FROM Professional p JOIN FETCH p.user u WHERE u.active = true")
    List<Professional> findAllWithActiveUsers();

    @Query("SELECT p FROM Professional p JOIN FETCH p.user u WHERE p.establishmentId = :establishmentId AND u.active = true")
    List<Professional> findByEstablishmentIdWithActiveUsers(@Param("establishmentId") Long establishmentId);

    void deleteByUserId(Long userId);

    List<Professional> findByEstablishmentIdIsNull();


}