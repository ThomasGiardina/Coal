package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.service.FavoritosService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.uade.tpo.demo.entity.Videojuego;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;



import java.util.List;

@RestController
@RequestMapping("/favoritos")
public class FavoritosController {

    @Autowired
    private FavoritosService favoritoService;

    @GetMapping
    public ResponseEntity<?> obtenerFavoritosDeUsuario(@RequestParam(required = false) Long usuarioId) {
        if (usuarioId == null) {
            return ResponseEntity.badRequest().body("El usuarioId es obligatorio.");
        }
        try {
            List<Videojuego> favoritos = favoritoService.obtenerFavoritosDeUsuario(usuarioId);
            return ResponseEntity.ok(favoritos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener favoritos");
        }
    }

    @PostMapping("/{videojuegoId}")
    public ResponseEntity<String> agregarAFavoritos(@RequestParam Long usuarioId, @PathVariable Long videojuegoId) {
        System.out.println("usuarioId: " + usuarioId + ", videojuegoId: " + videojuegoId);
        favoritoService.agregarAFavoritos(usuarioId, videojuegoId);
        return ResponseEntity.ok("Videojuego agregado a favoritos");
    }

    @DeleteMapping("/{videojuegoId}")
    public ResponseEntity<String> eliminarDeFavoritos(@RequestParam Long usuarioId, @PathVariable Long videojuegoId) {
        try {
            favoritoService.eliminarDeFavoritos(usuarioId, videojuegoId);
            return ResponseEntity.ok("Videojuego eliminado de favoritos");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al eliminar el videojuego");
        }
    }


}
