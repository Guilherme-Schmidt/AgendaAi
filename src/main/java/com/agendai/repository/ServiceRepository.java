package com.agendai.repository;

import com.agendai.model.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByEstablishmentIdAndActiveTrue(Long establishmentId);
    List<Service> findByCategoryIdAndActiveTrue(Long categoryId);
    Page<Service> findByActiveTrue(Pageable pageable);

    @Query("SELECT s FROM Service s WHERE s.establishment.id = :establishmentId " +
            "AND s.category.id = :categoryId AND s.active = true")
    List<Service> findByEstablishmentAndCategoryAndActiveTrue(
            @Param("establishmentId") Long establishmentId,
            @Param("categoryId") Long categoryId);

    @Query("SELECT s FROM Service s WHERE s.active = true AND s.price BETWEEN :minPrice AND :maxPrice")
    List<Service> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    boolean existsByNameAndEstablishmentIdAndActiveTrue(String name, Long establishmentId);

    @Query("SELECT s FROM Service s WHERE s.active = true AND " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Service> searchActiveServices(@Param("search") String search, Pageable pageable);
}