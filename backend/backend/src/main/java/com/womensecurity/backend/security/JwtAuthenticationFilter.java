package com.womensecurity.backend.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;


    // =====================================================
    // SKIP JWT CHECK FOR PUBLIC APIs
    // =====================================================

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)
            throws ServletException {

        String path = request.getServletPath();

        // Login
        if (path.startsWith("/api/auth/")) {
            return true;
        }

        // Forgot password / OTP
        if (path.startsWith("/api/password/")) {
            return true;
        }

        return false;
    }


    // =====================================================
    // JWT FILTER
    // =====================================================

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {


        String authHeader =
                request.getHeader("Authorization");


        String jwt = null;
        String email = null;


        // =================================================
        // CHECK BEARER TOKEN
        // =================================================

        if (authHeader != null &&
                authHeader.startsWith("Bearer ")) {

            jwt = authHeader.substring(7);

            try {

                email = jwtUtil.extractUsername(jwt);

            } catch (Exception e) {

                // Invalid token
                // Continue without authentication

                System.out.println(
                        "Invalid JWT token"
                );
            }
        }


        // =================================================
        // VALIDATE USER
        // =================================================

        if (email != null &&
                SecurityContextHolder
                        .getContext()
                        .getAuthentication() == null) {


            UserDetails userDetails =
                    userDetailsService
                            .loadUserByUsername(email);


            if (jwtUtil.validateToken(
                    jwt,
                    userDetails.getUsername())) {


                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );


                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );


                SecurityContextHolder
                        .getContext()
                        .setAuthentication(
                                authentication
                        );
            }
        }


        // =================================================
        // CONTINUE REQUEST
        // =================================================

        filterChain.doFilter(
                request,
                response
        );
    }
}