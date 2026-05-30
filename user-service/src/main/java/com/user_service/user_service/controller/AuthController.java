package com.user_service.user_service.controller;

import com.user_service.user_service.dto.*;
import com.user_service.user_service.security.JwtUtil;
import com.user_service.user_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refresh(
            @RequestBody RefreshTokenRequest req) {
        return ResponseEntity.ok(
                authService.refreshToken(req.getRefreshToken()));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(
                authService.getProfile(ud.getUsername()));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal UserDetails ud,
            @Valid @RequestBody UpdateProfileRequest req) {
        return ResponseEntity.ok(
                authService.updateProfile(ud.getUsername(), req));
    }

    // Internal endpoint for gateway to validate tokens
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validate(
            @RequestHeader("Authorization") String header) {
        return ResponseEntity.ok(
                jwtUtil.validateToken(header.replace("Bearer ", "")));
    }
}