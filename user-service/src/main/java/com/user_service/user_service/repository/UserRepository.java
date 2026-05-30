package com.user_service.user_service.repository;

import com.user_service.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);//checks existence only
    Optional<User> findByEmail(String email);//fetches full row.
}
