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
import com.uade.tpo.demo.entity.HistorialPedidos;
import com.uade.tpo.demo.entity.Rol;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.repository.UserRepository;
import com.uade.tpo.demo.repository.CarritoRepository;
import com.uade.tpo.demo.repository.HistorialPedidosRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; 

@Service
@RequiredArgsConstructor
@Slf4j  
public class AuthenticationService {
        private final UserRepository repository;
        private final CarritoRepository carritoRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;
        private final HistorialPedidosRepository historialPedidosRepository;

        public AuthenticationResponse register(RegisterRequest request) {
                log.info("Iniciando proceso de registro para: {}", request.getEmail());
                
                
                if (repository.existsByEmail(request.getEmail())) {
                        throw new RuntimeException("email_already_exists");
                }
                if (repository.existsByUsername(request.getUsername())) {
                        throw new RuntimeException("username_already_exists");
                }
                
                String imagenPerfil = "defaultUser.jpg";
                
                var user = Usuario.builder()
                        .username(request.getUsername())
                        .firstName(request.getFirstname())
                        .lastName(request.getLastname())
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .imagenPerfil(imagenPerfil)
                        .role(Rol.USER) 
                        .build();
                
                Usuario savedUser = repository.save(user);
                
                Carrito carrito = new Carrito();
                carrito.setUsuario(savedUser);
                
                HistorialPedidos historialPedidos = new HistorialPedidos();
                historialPedidos.setUsuario(savedUser);
                historialPedidosRepository.save(historialPedidos);
                
                carritoRepository.save(carrito);
                savedUser.setCarrito(carrito);
                repository.save(savedUser);
                
                var jwtToken = jwtService.generateToken(savedUser);
                log.info("Token generado para usuario {}: {}", savedUser.getUsername(), jwtToken);
                
                return AuthenticationResponse.builder()
                        .accessToken(jwtToken)
                        .role(savedUser.getRole().name())
                        .build();
        }

        public AuthenticationResponse authenticate(AuthenticationRequest request) {
                log.info("Autenticando usuario: {}", request.getEmail());  

                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()));
                
                var user = repository.findByEmail(request.getEmail())
                        .orElseThrow(() -> {
                        log.error("Usuario no encontrado: {}", request.getEmail()); 
                        return new RuntimeException("Usuario no encontrado");
                        });

                var jwtToken = jwtService.generateToken(user);
                log.info("Token generado para usuario {}: {}", user.getUsername(), jwtToken); 
                
                return AuthenticationResponse.builder()
                        .accessToken(jwtToken)
                        .role(user.getRole().name())  
                        .build();
        }
}
