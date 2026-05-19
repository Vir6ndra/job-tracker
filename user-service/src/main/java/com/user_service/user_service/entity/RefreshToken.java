package com.user_service.user_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class RefreshToken {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private User user;
//    One user can have multiple refresh tokens

    private String token;
    private LocalDateTime expiresAt;

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
