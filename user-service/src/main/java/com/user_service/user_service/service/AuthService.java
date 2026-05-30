package com.user_service.user_service.service;

import com.user_service.user_service.exception.EmailAlreadyExistsException;
import com.user_service.user_service.service.RefreshTokenService;
import com.user_service.user_service.dto.*;
import com.user_service.user_service.entity.RefreshToken;
import com.user_service.user_service.entity.User;
import com.user_service.user_service.repository.UserRepository;
import com.user_service.user_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    //    ##Register
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email already in use: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        user = userRepository.save(user);

        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getId());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    //##Login
    public AuthResponse login(LoginRequest request) {
        // Throws BadCredentialsException automatically if wrong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getId());
        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    //##refreshToken responce
    public RefreshTokenResponse refreshToken(String tokenStr) {
        RefreshToken rt = refreshTokenService.findByToken(tokenStr);
        refreshTokenService.verifyExpiry(rt);
        String newAccess = jwtUtil.generateAccessToken(rt.getUser().getEmail(), rt.getUser().getId());
        return new RefreshTokenResponse(newAccess);
    }

    //    //##get Profile
    public UserProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return toProfileResponse(user);
    }


    //    //##Update profile
    public UserProfileResponse updateProfile(String email,
                                             UpdateProfileRequest req) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(req.getName());
        return toProfileResponse(userRepository.save(user));
    }

    private UserProfileResponse toProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }


}


//@Service
//@RequiredArgsConstructor
//public class AuthService {
//
//    private final UserRepository userRepository;
//
//    private final PasswordEncoder passwordEncoder;
//
//    private final JwtUtil jwtUtil;
//
//    private final RefreshTokenService refreshTokenService;
//
//    //#Register Method#
//    public AuthResponse regirster(RegisterRequest request){
//        if(userRepository.existsByEmail(request.getEmail())){
//            throw new RuntimeException("Email already Exists");
//        }
//
//        User user = new User();
//        user.setName(request.getName());
//        user.setEmail(request.getEmail());
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
//        user.setCreatedAt(LocalDateTime.now());
//        userRepository.save(user);
//
//
//    }
//}
