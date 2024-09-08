package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Videojuego;
import com.uade.tpo.demo.entity.Videojuego.CategoriaJuego;
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
    Videojuego agregarStock(Long videojuegoId, int cantidad) throws VideojuegoNotFoundException;
    Videojuego disminuirStock(Long videojuegoId, int cantidad) throws VideojuegoNotFoundException;
    List<Videojuego> buscarPorTitulo(String titulo);
    List<Videojuego> buscarPorPlataforma(String plataforma);
    List<Videojuego> buscarPorRangoDePrecio(Double minPrecio, Double maxPrecio);
    List<Videojuego> buscarPorDescripcion(String keyword);
    List<Videojuego> buscarPorCategoria(CategoriaJuego categoria);
    Videojuego subirFoto(Long videojuegoId, MultipartFile foto) throws IOException, VideojuegoNotFoundException;
}