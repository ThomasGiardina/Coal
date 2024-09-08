package com.uade.tpo.demo.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.uade.tpo.demo.controllers.auth.AuthenticationRequest;
import com.uade.tpo.demo.controllers.auth.AuthenticationResponse;
import com.uade.tpo.demo.controllers.auth.RegisterRequest;
import com.uade.tpo.demo.controllers.config.JwtService;
import com.uade.tpo.demo.entity.Carrito;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.repository.UserRepository;
import com.uade.tpo.demo.repository.CarritoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final CarritoRepository carritoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        // Crear y guardar el usuario
        var user = Usuario.builder()
                .username(request.getUsername())
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        // Guarda el usuario primero
        Usuario savedUser = repository.save(user);

        // Crear y asociar el carrito
        Carrito carrito = new Carrito();
        carrito.setUsuario(savedUser);

        // Guarda el carrito en la base de datos
        carritoRepository.save(carrito);

        // Asocia el carrito al usuario y guarda nuevamente
        savedUser.setCarrito(carrito);
        repository.save(savedUser);

        // Generar el token JWT
        var jwtToken = jwtService.generateToken(savedUser);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }
}
