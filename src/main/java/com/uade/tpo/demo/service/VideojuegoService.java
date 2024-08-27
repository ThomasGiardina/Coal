package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Videojuego;
import com.uade.tpo.demo.exception.VideojuegoNotFoundException;
import com.uade.tpo.demo.repository.VideojuegoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VideojuegoService {

    private final VideojuegoRepository videojuegoRepository;

    // Inyección de dependencias a través del constructor
    public VideojuegoService(VideojuegoRepository videojuegoRepository) {
        this.videojuegoRepository = videojuegoRepository;
    }

    // Método para agregar un nuevo videojuego
    public Videojuego crearVideojuego(Videojuego videojuego) {
        return videojuegoRepository.save(videojuego);
    }

    // Método para obtener un videojuego por ID
    public Videojuego obtenerVideojuegoPorId(Long videojuegoId) throws VideojuegoNotFoundException {
        return videojuegoRepository.findById(videojuegoId)
                .orElseThrow(() -> new VideojuegoNotFoundException("Videojuego no encontrado"));
    }

    // Método para obtener todos los videojuegos
    public List<Videojuego> obtenerTodosLosVideojuegos() {
        return videojuegoRepository.findAll();
    }

    // Método para actualizar un videojuego existente
    public Videojuego actualizarVideojuego(Long videojuegoId, Videojuego datosActualizados) throws VideojuegoNotFoundException {
        Videojuego videojuego = obtenerVideojuegoPorId(videojuegoId);
        videojuego.setTitulo(datosActualizados.getTitulo());
        videojuego.setDescripcion(datosActualizados.getDescripcion());
        videojuego.setPrecio(datosActualizados.getPrecio());
        videojuego.setPlataforma(datosActualizados.getPlataforma());
        videojuego.setStock(datosActualizados.getStock());
        return videojuegoRepository.save(videojuego);
    }

    // Método para eliminar un videojuego
    public void eliminarVideojuego(Long videojuegoId) throws VideojuegoNotFoundException {
        Videojuego videojuego = obtenerVideojuegoPorId(videojuegoId);
        videojuegoRepository.delete(videojuego);
    }

    // Método para agregar stock a un videojuego
    public Videojuego agregarStock(Long videojuegoId, int cantidad) throws VideojuegoNotFoundException {
        Videojuego videojuego = obtenerVideojuegoPorId(videojuegoId);
        videojuego.setStock(videojuego.getStock() + cantidad);
        return videojuegoRepository.save(videojuego);
    }

    // Método para disminuir stock de un videojuego
    public Videojuego disminuirStock(Long videojuegoId, int cantidad) throws VideojuegoNotFoundException {
        Videojuego videojuego = obtenerVideojuegoPorId(videojuegoId);
        if (videojuego.getStock() < cantidad) {
            throw new IllegalArgumentException("No hay suficiente stock disponible.");
        }
        videojuego.setStock(videojuego.getStock() - cantidad);
        return videojuegoRepository.save(videojuego);
    }

    // Método para buscar videojuegos por título
    public List<Videojuego> buscarPorTitulo(String titulo) {
        return videojuegoRepository.findByTitulo(titulo);
    }

    // Método para buscar videojuegos por plataforma
    public List<Videojuego> buscarPorPlataforma(String plataforma) {
        return videojuegoRepository.findByPlataforma(plataforma);
    }

    // Método para buscar videojuegos por rango de precio
    public List<Videojuego> buscarPorRangoDePrecio(Double minPrecio, Double maxPrecio) {
        return videojuegoRepository.findByPrecioBetween(minPrecio, maxPrecio);
    }

    // Método para buscar videojuegos por palabra clave en la descripción
    public List<Videojuego> buscarPorDescripcion(String keyword) {
        return videojuegoRepository.findByDescripcionContaining(keyword);
    }
}
