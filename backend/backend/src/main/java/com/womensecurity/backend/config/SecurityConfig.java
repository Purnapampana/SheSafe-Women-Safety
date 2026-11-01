package com.womensecurity.backend.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.womensecurity.backend.security.JwtAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {


    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {


        http

            // Disable CSRF because we are using JWT
            .csrf(csrf -> csrf.disable())


            // Enable CORS
            .cors(Customizer.withDefaults())


            // JWT authentication is stateless
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )


            // ===============================
            // AUTHORIZATION
            // ===============================

            .authorizeHttpRequests(auth -> auth


                // Allow browser CORS preflight
                .requestMatchers(
                    HttpMethod.OPTIONS,
                    "/**"
                ).permitAll()

               
                // LOGIN + REGISTER
                .requestMatchers(
                    "/api/auth/**"
                ).permitAll()


                // FORGOT PASSWORD + OTP
                .requestMatchers(
                    "/api/password/**"
                ).permitAll()
                .requestMatchers("/actuator/health").permitAll()
              // SUPER ADMIN APIs
                .requestMatchers("/api/admin/**").hasRole("SUPER_ADMIN")

.requestMatchers("/api/ai/**").authenticated()
                // All remaining APIs require JWT
                .anyRequest().authenticated()
            );


        // ===============================
        // JWT FILTER
        // ===============================

        http.addFilterBefore(
            jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class
        );


        return http.build();
    }


    // ===============================
    // PASSWORD ENCODER
    // ===============================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    // ===============================
    // AUTHENTICATION MANAGER
    // ===============================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }


    // ===============================
    // CORS CONFIGURATION
    // ===============================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();


        configuration.setAllowedOriginPatterns(
                Arrays.asList("*")
        );


        configuration.setAllowedMethods(
                Arrays.asList(
                    "GET",
                    "POST",
                    "PUT",
                    "DELETE",
                    "OPTIONS"
                )
        );


        configuration.setAllowedHeaders(
                Arrays.asList("*")
        );


        configuration.setAllowCredentials(false);


        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();


        source.registerCorsConfiguration(
                "/**",
                configuration
        );


        return source;
    }
}