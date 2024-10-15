package com.uade.tpo.demo.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.demo.dto.UsuarioDTO;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.service.UsuarioService;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PutMapping("/actualizar")
    public ResponseEntity<Usuario> actualizarUsuario(@RequestBody Usuario usuario) {
        try {
            Usuario usuarioActualizado = usuarioService.actualizarUsuario(usuario);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/actual")
    public ResponseEntity<UsuarioDTO> obtenerUsuarioActual() {
        try {
            Usuario usuarioActual = usuarioService.obtenerUsuarioActual();
            UsuarioDTO usuarioDTO = new UsuarioDTO(
                usuarioActual.getId(),
                usuarioActual.getRealUsername(),
                usuarioActual.getEmail(),
                usuarioActual.getFirstName(),
                usuarioActual.getLastName(),
                usuarioActual.getTelefono()
            );
            return ResponseEntity.ok(usuarioDTO);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @Value("${upload.dir}") // Inyectamos el directorio de subida desde application.properties
    private String uploadDir;

    @PostMapping("/actualizar-imagen")
public ResponseEntity<String> actualizarImagenPerfil(@RequestParam("imagen") MultipartFile file) {
    try {
        Usuario usuarioActual = usuarioService.obtenerUsuarioActual();

        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        usuarioActual.setImagenPerfil(fileName); // Guardar el nombre del archivo
        usuarioService.actualizarUsuario(usuarioActual);

        return ResponseEntity.ok(fileName); // Devolver el nombre de la imagen
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la imagen.");
    }
}

    @GetMapping("/imagen/{nombreImagen}")
    public ResponseEntity<Resource> obtenerImagen(@PathVariable String nombreImagen) {
        try {
            Path filePath = Paths.get(uploadDir, nombreImagen);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // Cambiar el tipo de contenido si es PNG o JPEG
                        .body(resource);
            } else {
                throw new RuntimeException("No se pudo leer la imagen.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
