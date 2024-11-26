package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Favoritos;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.Videojuego;
import com.uade.tpo.demo.repository.FavoritosRepository;
import com.uade.tpo.demo.repository.UserRepository;
import com.uade.tpo.demo.repository.VideojuegoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
public class FavoritosService {

    @Autowired
    private FavoritosRepository favoritosRepository;

    @Autowired
    private VideojuegoRepository videojuegoRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Videojuego> obtenerFavoritosDeUsuario(Long usuarioId) {
        return favoritosRepository.findByUsuarioId(usuarioId)
            .stream()
            .map(Favoritos::getVideojuego)
            .collect(Collectors.toList());
    }

    public void agregarAFavoritos(Long usuarioId, Long videojuegoId) {
        if (favoritosRepository.existsByUsuarioIdAndVideojuegoId(usuarioId, videojuegoId)) {
            System.out.println("El videojuego ya está en favoritos.");
            return;
        }
    
        Usuario usuario = userRepository.findById(usuarioId)
            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        Videojuego videojuego = videojuegoRepository.findById(videojuegoId)
            .orElseThrow(() -> new EntityNotFoundException("Videojuego no encontrado"));
    
        Favoritos favorito = new Favoritos();
        favorito.setUsuario(usuario);
        favorito.setVideojuego(videojuego);
        favoritosRepository.save(favorito);
    }    

    @Transactional
    public void eliminarDeFavoritos(Long usuarioId, Long videojuegoId) {
        if (!favoritosRepository.existsByUsuarioIdAndVideojuegoId(usuarioId, videojuegoId)) {
            System.out.println("El videojuego no está en favoritos, no es necesario eliminarlo.");
            return;
        }
        favoritosRepository.deleteByUsuarioIdAndVideojuegoId(usuarioId, videojuegoId);
    }
}