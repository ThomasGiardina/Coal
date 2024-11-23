package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Videojuego;
import com.uade.tpo.demo.exception.VideojuegoNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface VideojuegoService {
    Videojuego crearVideojuego(Videojuego videojuego);
    Videojuego obtenerVideojuegoPorId(Long videojuegoId) throws VideojuegoNotFoundException;
    List<Videojuego> obtenerTodosLosVideojuegos();
    Videojuego actualizarVideojuego(Long videojuegoId, Videojuego datosActualizados) throws VideojuegoNotFoundException;
    void eliminarVideojuego(Long videojuegoId) throws VideojuegoNotFoundException;
    Videojuego subirFoto(Long videojuegoId, MultipartFile foto) throws IOException, VideojuegoNotFoundException;
    Videojuego subirCarruselImagen(Long videojuegoId, MultipartFile imagen, int index) throws IOException, VideojuegoNotFoundException;
}
