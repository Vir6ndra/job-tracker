package com.user_service.user_service.service;

import com.user_service.user_service.entity.RefreshToken;
import com.user_service.user_service.entity.User;
import com.user_service.user_service.entity.RefreshToken;
import com.user_service.user_service.entity.User;
import com.user_service.user_service.exception.TokenExpiredException;
import com.user_service.user_service.repository.RefreshTokenRepository;
import com.user_service.user_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final JwtUtil jwtUtil;

    @Value("${jwt.refresh-token-expiry}")
    private long refreshTokenExpiry;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // Delete old token(s) for this user first
        repo.deleteByUserId(user.getId());

        String tokenStr = jwtUtil.generateRefreshToken(user.getEmail());

        RefreshToken rt = RefreshToken.builder()
                .user(user)
                .token(tokenStr)
                .expiresAt(LocalDateTime.now()
                        .plusSeconds(refreshTokenExpiry / 1000))
                .build();

        return repo.save(rt);
    }

    public RefreshToken verifyExpiry(RefreshToken token) {
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            repo.delete(token);
            throw new TokenExpiredException(
                    "Refresh token expired. Please login again.");
        }
        return token;
    }

    public RefreshToken findByToken(String token) {
        return repo.findByToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Invalid refresh token"));
    }
}



//
//@Service
//@RequiredArgsConstructor
//public class RefreshTokenService {
//
//    private final RefreshTokenRepository refreshTokenRepository;
//
//    public RefreshToken createRefreshToken(User user, String token){
//        RefreshToken refreshToken = new RefreshToken();
//        refreshToken.setUser(user);
//        refreshToken.setToken(token);
//        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
//
//        return refreshTokenRepository.save(refreshToken);
//    }
//
//    public boolean verifyExpiry(RefreshToken token){
//        return token.getExpiresAt().isAfter(LocalDateTime.now());
//    }
//}


//POST /refresh-token
//      ↓
//find token in DB
//      ↓
//verifyExpiry()
//      ↓
//generate new access token
