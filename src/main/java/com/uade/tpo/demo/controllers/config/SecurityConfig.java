package com.uade.tpo.demo.controllers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(req -> req
                .requestMatchers("/api/v1/auth/**").permitAll() // Permitir acceso sin autenticación a los endpoints de autenticación
                .requestMatchers("/videojuegos/**").permitAll() // Permitir acceso sin autenticación a los endpoints de videojuegos
                .requestMatchers("/carritos/**").permitAll() // Permitir acceso sin autenticación a los endpoints de carritos
                .requestMatchers("/api/metodos-pago/**").permitAll() // Permitir acceso sin autenticación a los endpoints de métodos de pago
                .anyRequest().authenticated()) // Requerir autenticación para cualquier otro endpoint
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}