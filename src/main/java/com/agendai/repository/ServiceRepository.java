package com.agendai.repository;

import com.agendai.model.Service;
import com.agendai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByEstablishmentIdAndActiveTrue(Long establishmentId);

    // CORRIGIDO: Mudança de p.establishment.id para p.establishmentId
    @Query("SELECT u FROM User u JOIN u.professional p WHERE p.establishmentId = :establishmentId AND u.active = true")
    List<User> findProfessionalsByEstablishment(@Param("establishmentId") Long establishmentId);

    List<Service> findByCategoryIdAndActiveTrue(Long categoryId);

    List<Service> findByActiveTrue();

    @Query("SELECT s FROM Service s WHERE s.establishmentId = :establishmentId AND s.categoryId = :categoryId AND s.active = true")
    List<Service> findByEstablishmentAndCategory(@Param("establishmentId") Long establishmentId,
                                                 @Param("categoryId") Long categoryId);

    boolean existsByNameAndEstablishmentId(String name, Long establishmentId);
}