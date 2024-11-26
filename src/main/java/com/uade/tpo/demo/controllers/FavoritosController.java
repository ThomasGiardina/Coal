package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.entity.Favoritos;
import com.uade.tpo.demo.service.FavoritosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
public class FavoritosController {

    @Autowired
    private FavoritosService favoritosService;

    @GetMapping("/{usuarioId}")
    public List<Favoritos> getFavoritosByUsuario(@PathVariable Long usuarioId) {
        return favoritosService.getFavoritosByUsuarioId(usuarioId);
    }

    @PostMapping
    public String addFavorito(@RequestParam Long usuarioId, @RequestParam Long videojuegoId) {
        favoritosService.addFavorito(usuarioId, videojuegoId);
        return "Videojuego agregado a favoritos.";
    }

    @DeleteMapping
    public String removeFavorito(@RequestParam Long usuarioId, @RequestParam Long videojuegoId) {
        favoritosService.removeFavorito(usuarioId, videojuegoId);
        return "Videojuego eliminado de favoritos.";
    }
}
