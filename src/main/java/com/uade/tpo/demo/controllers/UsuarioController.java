package com.uade.tpo.demo.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

