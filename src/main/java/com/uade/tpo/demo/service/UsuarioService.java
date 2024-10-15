package com.uade.tpo.demo.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.exception.UserNotFoundException;
import com.uade.tpo.demo.repository.UserRepository;

@Service
public class UsuarioService {

    @Autowired
    private UserRepository usuarioRepository;

    // Método para actualizar los datos del usuario
    public Usuario actualizarUsuario(Usuario usuario) {
        if (usuario.getFirstName() == null || usuario.getFirstName().isEmpty()) {
            throw new RuntimeException("El campo firstName no puede estar vacío.");
        }
        if (usuario.getLastName() == null || usuario.getLastName().isEmpty()) {
            throw new RuntimeException("El campo lastName no puede estar vacío.");
        }
        return usuarioRepository.findById(usuario.getId())  // Encuentra el usuario por ID
                .map(usuarioExistente -> {
                    usuarioExistente.setUsername(usuario.getUsername());
                    usuarioExistente.setFirstName(usuario.getFirstName());
                    usuarioExistente.setLastName(usuario.getLastName());
                    usuarioExistente.setEmail(usuario.getEmail());
                    if (usuario.getTelefono() != null) {
                        usuarioExistente.setTelefono(usuario.getTelefono());
                    }  // Manejar nulos si es necesario
                    return usuarioRepository.save(usuarioExistente);
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // Método para obtener el usuario actual
    public Usuario obtenerUsuarioActual() {
        String emailUsuarioActual = obtenerEmailUsuarioActual();
        return usuarioRepository.findByEmail(emailUsuarioActual)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con el email: " + emailUsuarioActual));
    }

    // Método para obtener el email del usuario actual autenticado
    public String obtenerEmailUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();  // Aquí devuelves el email del usuario autenticado
        } else {
            throw new RuntimeException("No se pudo autenticar al usuario.");
        }
    }

    @Value("${upload.dir}") // Inyectar la propiedad del directorio de subida desde application.properties
    private String uploadDir;

    // Guardar la imagen en el sistema de archivos y actualizar la ruta en el usuario
    public Usuario actualizarImagenUsuario(MultipartFile imagen) throws IOException {
        // Obtener el usuario actual autenticado
        Usuario usuario = obtenerUsuarioActual();

        // Guardar la imagen en el directorio especificado
        String fileName = imagen.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.copy(imagen.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Actualizar la ruta de la imagen en el usuario
        usuario.setImagenPerfil(fileName);  // Guardamos solo el nombre del archivo
        return usuarioRepository.save(usuario); // Guardamos la información actualizada del usuario
    }

}
