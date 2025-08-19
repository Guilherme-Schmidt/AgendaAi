package com.agendai.repository;

import com.agendai.model.Establishment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstablishmentRepository extends JpaRepository<Establishment, Long> {

    List<Establishment> findByOwnerIdAndActiveTrue(Long ownerId);
    Page<Establishment> findByActiveTrue(Pageable pageable);

    @Query("SELECT e FROM Establishment e WHERE e.active = true AND " +
            "(LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(e.address) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Establishment> searchActiveEstablishments(@Param("search") String search, Pageable pageable);

    @Query("SELECT COUNT(e) FROM Establishment e WHERE e.owner.id = :ownerId AND e.active = true")
    long countByOwnerIdAndActiveTrue(@Param("ownerId") Long ownerId);
}