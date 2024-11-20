package com.uade.tpo.demo.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.CacheControl;
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @PostMapping("/actualizar-imagen")
    public ResponseEntity<String> actualizarImagenPerfil(@RequestParam("imagen") MultipartFile file) {
        try {
            long maxFileSize = 100 * 1024 * 1024; 
            if (file.getSize() > maxFileSize) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El archivo excede el tamaño máximo permitido.");
            }

            Usuario usuarioActual = usuarioService.obtenerUsuarioActual();
            usuarioActual.setImagenPerfil(file.getBytes());
            usuarioService.actualizarUsuario(usuarioActual);

            return ResponseEntity.ok("Imagen actualizada correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la imagen.");
        }
    }

    @GetMapping("/imagen/{userId}")
    public ResponseEntity<byte[]> obtenerImagen(@PathVariable Long userId) {
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorId(userId);

            if (usuario.getImagenPerfil() == null) {
                throw new RuntimeException("Imagen no encontrada para el usuario.");
            }

            byte[] imagenPerfil = usuario.getImagenPerfil();
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) 
                    .body(imagenPerfil);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/cambiar-contrasena")
    public ResponseEntity<String> cambiarContrasena(
            @RequestParam("contraseñaActual") String contraseñaActual,
            @RequestParam("nuevaContraseña") String nuevaContraseña) {
        try {
            usuarioService.cambiarContrasena(contraseñaActual, nuevaContraseña);
            return ResponseEntity.ok("Contraseña cambiada exitosamente.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al cambiar la contraseña.");
        }
    }
}
