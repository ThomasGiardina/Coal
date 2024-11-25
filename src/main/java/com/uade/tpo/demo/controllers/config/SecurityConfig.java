package com.uade.tpo.demo.controllers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))  
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("GET", "/videojuegos/**").permitAll()
                .requestMatchers("POST", "/videojuegos/**").hasRole("ADMIN")
                .requestMatchers("PUT", "/videojuegos/**").hasRole("ADMIN")
                .requestMatchers("DELETE", "/videojuegos/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/usuario/olvidar-contrasena").permitAll()
                .requestMatchers("/carritos/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/api/pedidos/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/metodosPago/**").hasRole("USER")
                .requestMatchers("/error").permitAll()
                .requestMatchers("/Historial/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/fotos/**").hasRole("ADMIN")
                .requestMatchers("/api/usuario/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/estadisticas/**").hasRole("ADMIN")
                .anyRequest().authenticated()) 
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); 

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
