package com.agendai.repository;

import com.agendai.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNameAndActiveTrue(String name);
    List<Category> findByActiveTrueOrderByName();
    Page<Category> findByActiveTrue(Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.active = true AND " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Category> searchActiveCategories(@Param("search") String search);
}