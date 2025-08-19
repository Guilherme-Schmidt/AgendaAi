package com.agendai.repository;

import com.agendai.model.Professional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import com.agendai.model.Professional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessionalRepository extends JpaRepository<Professional, Long> {

    Optional<Professional> findByUserId(Long userId);
    boolean existsByUserId(Long userId);

    @Query("SELECT p FROM Professional p JOIN FETCH p.user u WHERE u.active = true")
    List<Professional> findAllWithActiveUsers();

    @Query("SELECT p FROM Professional p JOIN FETCH p.user u WHERE u.active = true")
    Page<Professional> findAllWithActiveUsers(Pageable pageable);

    @Query("SELECT p FROM Professional p JOIN FETCH p.user u " +
            "WHERE p.establishment.id = :establishmentId AND u.active = true")
    List<Professional> findByEstablishmentIdWithActiveUsers(@Param("establishmentId") Long establishmentId);

    @Query("SELECT p FROM Professional p JOIN FETCH p.user u " +
            "WHERE p.establishment IS NULL AND u.active = true")
    List<Professional> findAvailableProfessionals();

    @Query("SELECT p FROM Professional p JOIN FETCH p.user u " +
            "WHERE p.available = true AND u.active = true")
    List<Professional> findByAvailableTrue();

    void deleteByUserId(Long userId);
}
