package com.user_service.user_service.service;

import com.user_service.user_service.entity.RefreshToken;
import com.user_service.user_service.entity.User;
import com.user_service.user_service.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createRefreshToken(User user, String token){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));

        return refreshTokenRepository.save(refreshToken);
    }

    public boolean verifyExpiry(RefreshToken token){
        return token.getExpiresAt().isAfter(LocalDateTime.now());
    }
}


//POST /refresh-token
//      ↓
//find token in DB
//      ↓
//verifyExpiry()
//      ↓
//generate new access token
