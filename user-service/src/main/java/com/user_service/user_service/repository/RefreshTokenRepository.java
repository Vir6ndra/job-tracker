package com.user_service.user_service.repository;

import com.user_service.user_service.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

//    Frontend sends refresh token so we search DB for matching token
}
