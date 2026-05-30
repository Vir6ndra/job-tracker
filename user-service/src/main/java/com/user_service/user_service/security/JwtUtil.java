package com.user_service.user_service.security;
//
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
//import java.security.Key;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token-expiry}")
    private long accessTokenExpiry;

    @Value("${jwt.refresh-token-expiry}")
    private long refreshTokenExpiry;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

//     application-service needs the userId (UUID) to link applications to users. By embedding userId in the JWT claim,
//     the gateway can extract it and forward it as a header — no extra DB call needed
    public String generateAccessToken(String email, UUID userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString()); // embed userId so gateway can extract it
        return buildToken(claims, email, accessTokenExpiry);
    }


    public String generateRefreshToken(String email) {
        return buildToken(new HashMap<>(), email, refreshTokenExpiry);
    }

    private String buildToken(Map<String, Object> claims,
                              String subject, long expiry) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiry))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiry = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();
            return expiry.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
}


//
//@Component
//public class JwtUtil {
//
//    @Value("${jwt.secret}")
//    private String SECRET;
//
//    @Value("${jwt.access-token-expiry}")
//    private long ACCESS_TOKEN_EXPIRY;
//
//    @Value("${jwt.refresh-token-expiry}")
//    private long REFRESH_TOKEN_EXPIRY;
//
//    private SecretKey Key;
//
//    private Key getSignKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
//
//    public String generateAccessToken(String email){
//        return Jwts.builder()
//                .setSubject(email)
//                .setIssuedAt(new Date())
//                .setExpiration(
//                        new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY)
//                )
//                .signWith(getSignKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    public String generateRefreshToken(String email){
//        return Jwts.builder()
//                .setSubject(email)
//                .setIssuedAt(new Date())
//                .setExpiration(
//                        new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY)
//                )
//                .signWith(getSignKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    public Claims extractClaims(String token){
//
//        return Jwts.parserBuilder()
//                .setSigningKey(getSignKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    public String extractEmail(String token){
//        return extractClaims(token).getSubject();
//    }
//
//    public boolean isTokenValid(String token) {
//        try {
//            extractClaims(token);
//            return true;
//        } catch (JwtException e) {
//            return false;
//        }
//    }
//
//}
