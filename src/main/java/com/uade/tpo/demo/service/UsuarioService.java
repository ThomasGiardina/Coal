package com.uade.tpo.demo.service;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.exception.UserNotFoundException;
import com.uade.tpo.demo.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UsuarioService {

    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario actualizarUsuario(Usuario usuario) {
        log.info("Iniciando actualización del usuario con ID: {}", usuario.getId());  
    
        return usuarioRepository.findById(usuario.getId())  
            .map(usuarioExistente -> {
                log.info("Usuario encontrado: {}", usuarioExistente);
    
                // Actualizando el username si es necesario
                if (usuario.getRealUsername() != null && !usuario.getRealUsername().isEmpty()) {
                    log.info("Actualizando username de {} a {}", usuarioExistente.getUsername(), usuario.getRealUsername());
                    usuarioExistente.setUsername(usuario.getRealUsername());
                }
    
                // Actualizando el email si es necesario
                if (usuario.getEmail() != null && !usuario.getEmail().isEmpty()) {
                    log.info("Actualizando email de {} a {}", usuarioExistente.getEmail(), usuario.getEmail());
                    usuarioExistente.setEmail(usuario.getEmail());
                }
    
                // Actualizando el firstName si es necesario
                if (usuario.getFirstName() != null && !usuario.getFirstName().isEmpty()) {
                    log.info("Actualizando firstName de {} a {}", usuarioExistente.getFirstName(), usuario.getFirstName());
                    usuarioExistente.setFirstName(usuario.getFirstName());
                }
    
                // Actualizando el lastName si es necesario
                if (usuario.getLastName() != null && !usuario.getLastName().isEmpty()) {
                    log.info("Actualizando lastName de {} a {}", usuarioExistente.getLastName(), usuario.getLastName());
                    usuarioExistente.setLastName(usuario.getLastName());
                }
    
                // Actualizando el telefono si es necesario
                if (usuario.getTelefono() != null && !usuario.getTelefono().isEmpty()) {
                    log.info("Actualizando telefono de {} a {}", usuarioExistente.getTelefono(), usuario.getTelefono());
                    usuarioExistente.setTelefono(usuario.getTelefono());
                }
    
                log.info("Guardando cambios para el usuario: {}", usuarioExistente);
                return usuarioRepository.save(usuarioExistente);
            })
            .orElseThrow(() -> {
                log.error("Usuario con ID {} no encontrado", usuario.getId());
                return new RuntimeException("Usuario no encontrado");
            });
    }

    public Usuario obtenerUsuarioActual() {
        String emailUsuarioActual = obtenerEmailUsuarioActual();
        return usuarioRepository.findByEmail(emailUsuarioActual)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con el email: " + emailUsuarioActual));
    }

    public String obtenerEmailUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();  
        } else {
            throw new RuntimeException("No se pudo autenticar al usuario.");
        }
    }

    @Value("${upload.dir}") 
    private String uploadDir;

    public Usuario actualizarImagenUsuario(MultipartFile imagen) throws IOException {
        Usuario usuario = obtenerUsuarioActual();
    
        usuario.setImagenPerfil(imagen.getBytes());
    
        return usuarioRepository.save(usuario);
    }

    public void cambiarContrasena(String contraseñaActual, String nuevaContraseña) {
        Usuario usuario = obtenerUsuarioActual();

        if (!passwordEncoder.matches(contraseñaActual, usuario.getPassword())) {
            throw new BadCredentialsException("La contraseña actual es incorrecta.");
        }

        usuario.setPassword(passwordEncoder.encode(nuevaContraseña));
        usuarioRepository.save(usuario);
    }

    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + id));
    }

    public boolean cambiarContrasenaPorEmail(String email, String nuevaContrasena) {
        return usuarioRepository.findByEmail(email)
            .map(usuario -> {
                usuario.setPassword(passwordEncoder.encode(nuevaContrasena));
                usuarioRepository.save(usuario);
                return true;
            })
            .orElse(false);
    }

}
