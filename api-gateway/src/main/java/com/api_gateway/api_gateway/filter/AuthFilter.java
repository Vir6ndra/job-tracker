package com.api_gateway.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GlobalFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.List;

@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String secret;

    private static final List<String> OPEN_PATHS = List.of(
            "/api/users/register",
            "/api/users/login",
            "/api/users/refresh-token"
    );

    private Key getSignKey() {

        byte[] keyBytes = Decoders.BASE64.decode(secret);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain
    ) {

        String path =
                exchange.getRequest().getPath().value();

        // Skip public endpoints
        if (OPEN_PATHS.stream().anyMatch(path::startsWith)) {

            return chain.filter(exchange);
        }

        String authHeader =
                exchange.getRequest()
                        .getHeaders()
                        .getFirst("Authorization");

        // Check header existence
        if (!StringUtils.hasText(authHeader)
                || !authHeader.startsWith("Bearer ")) {

            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

//        try {
//
//            Claims claims = Jwts.parserBuilder()
//                    .setSigningKey(getSignKey())
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//
//        } catch (JwtException e) {
//
//            exchange.getResponse()
//                    .setStatusCode(HttpStatus.UNAUTHORIZED);
//
//            return exchange.getResponse().setComplete();
//        }


        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Extract userId embedded by user-service and forward downstream
            String userId = claims.get("userId", String.class);

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header("X-User-Id", userId)
                            .build())
                    .build();

            return chain.filter(mutatedExchange); // pass mutated request forward

        } catch (JwtException e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
//        The gateway is the security boundary. Once it validates the JWT, it extracts the userId and adds it as
//         X-User-Id header. Every downstream microservice (application-service, company-service etc.)
//      reads this header instead of re-validating the JWT. This is standard microservice security pattern.


        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {

        return -1;
    }
}
