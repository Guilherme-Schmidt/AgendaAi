package com.agendai.repository;

import com.agendai.model.Establishment;
import com.agendai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EstablishmentRepository extends JpaRepository<Establishment, Long> {

    List<Establishment> findByOwner(User owner);

    List<Establishment> findByOwnerId(Long ownerId);

    @Query("SELECT e FROM Establishment e WHERE e.owner.id = :ownerId ORDER BY e.name")
    List<Establishment> findByOwnerIdOrderByName(@Param("ownerId") Long ownerId);

    @Query("SELECT e FROM Establishment e ORDER BY e.name")
    List<Establishment> findAllOrderByName();

    @Query("SELECT e FROM Establishment e WHERE " +
            "LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(e.address) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Establishment> searchEstablishments(@Param("search") String search);
}