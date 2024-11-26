package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Favoritos;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.Videojuego;
import com.uade.tpo.demo.repository.FavoritosRepository;
import com.uade.tpo.demo.repository.UserRepository;
import com.uade.tpo.demo.repository.VideojuegoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoritosService {

    @Autowired
    private FavoritosRepository favoritosRepository;

    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
    private VideojuegoRepository videojuegoRepository;

    public List<Favoritos> getFavoritosByUsuarioId(Long usuarioId) {
        return favoritosRepository.findByUsuarioId(usuarioId);
    }

    public void addFavorito(Long usuarioId, Long videojuegoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
        Videojuego videojuego = videojuegoRepository.findById(videojuegoId)
                .orElseThrow(() -> new RuntimeException("Videojuego no encontrado."));

        if (favoritosRepository.findByUsuarioIdAndVideojuegoId(usuarioId, videojuegoId).isPresent()) {
            throw new RuntimeException("El videojuego ya está en favoritos.");
        }

        Favoritos favorito = new Favoritos();
        favorito.setUsuario(usuario);
        favorito.setVideojuego(videojuego);
        favoritosRepository.save(favorito);
    }

    public void removeFavorito(Long usuarioId, Long videojuegoId) {
        favoritosRepository.deleteByUsuarioIdAndVideojuegoId(usuarioId, videojuegoId);
    }
}
