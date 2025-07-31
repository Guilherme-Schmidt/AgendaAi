package com.agendai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agendai.model.User;

public interface UserRepository extends JpaRepository <User, Long>{
    boolean existsByEmail(String email);
}
