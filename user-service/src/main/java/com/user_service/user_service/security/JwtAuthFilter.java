package com.user_service.user_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.user_service.user_service.security.JwtUtil;
import com.user_service.user_service.security.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null && jwtUtil.validateToken(token)) {
            String email = jwtUtil.extractEmail(token);

            if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}



//@Component
//@RequiredArgsConstructor
//public class JwtAuthFilter extends OncePerRequestFilter {
//
////    it runs once per request
//
//    private final JwtUtil jwtUtil;
//
//    private final UserDetailsServiceImpl userDetailsService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException,IOException {
//
//        String authHeader = request.getHeader("Authorization");
//
//        String token  = null;
//
//        String email = null;
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            token = authHeader.substring(7);
//            email = jwtUtil.extractEmail(token);
//        }
//
//
//        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//
////            we check getAuthentication() == null Because we should not authenticate same request twice
//
//            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
////            Fetch user from DB using email
//
//            if (jwtUtil.isTokenValid(token)) {
//                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                        userDetails, null, userDetails.getAuthorities());
//
////                here we are creating Spring Security authentication object
////                We create an Authentication object so Spring Security recognizes the user as authenticated for the current request
//
////                coz Spring Security itself does NOT understand JWT directlySo after validating JWT, we must convert user info into Spring Security’s format
//
////                this object means  - this user is authenticated
//
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
////                adds extra request information to authentication object.
////                Things like: IP address , session id , request metadata
////                so Spring Security can later use this for: auditing , logging , suspicious login detection
//
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//
////                we do this to store authenticated user globally for current request
////                now spring knows who's logged in
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//}



//JWT token
//   ↓
//extract email
//   ↓
//load user from DB
//   ↓
//validate token
//   ↓
//create Authentication object
//   ↓
//store in SecurityContextHolder
//   ↓
//request becomes authenticated
