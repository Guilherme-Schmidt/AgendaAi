package com.agendai.repository;

import com.agendai.model.User;
import com.agendai.model.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    Page<User> findByActiveTrue(Pageable pageable);
    Page<User> findByTypeAndActiveTrue(UserType type, Pageable pageable);

    List<User> findByTypeAndActiveTrue(UserType type);

    @Query("SELECT u FROM User u WHERE u.active = true AND " +
            "(LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> searchActiveUsers(@Param("search") String search, Pageable pageable);
}
