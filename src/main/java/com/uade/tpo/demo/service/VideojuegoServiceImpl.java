package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Videojuego;
import com.uade.tpo.demo.entity.Videojuego.CategoriaJuego;
import com.uade.tpo.demo.exception.VideojuegoNotFoundException;
import com.uade.tpo.demo.repository.VideojuegoRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class VideojuegoServiceImpl implements VideojuegoService {

    private final VideojuegoRepository videojuegoRepository;

    public VideojuegoServiceImpl(VideojuegoRepository videojuegoRepository) {
        this.videojuegoRepository = videojuegoRepository;
    }

    @Override
    public Videojuego crearVideojuego(Videojuego videojuego) {
        return videojuegoRepository.save(videojuego);
    }

    @Override
    public Videojuego obtenerVideojuegoPorId(Long videojuegoId) throws VideojuegoNotFoundException {
        return videojuegoRepository.findById(videojuegoId)
                .orElseThrow(() -> new VideojuegoNotFoundException("Videojuego no encontrado"));
    }

    @Override
    public List<Videojuego> obtenerTodosLosVideojuegos() {
        return videojuegoRepository.findAll();
    }

    @Override
    public Videojuego actualizarVideojuego(Long videojuegoId, Videojuego datosActualizados) throws VideojuegoNotFoundException {
        Videojuego videojuego = obtenerVideojuegoPorId(videojuegoId);
        videojuego.setTitulo(datosActualizados.getTitulo());
        videojuego.setDescripcion(datosActualizados.getDescripcion());
        videojuego.setPrecio(datosActualizados.getPrecio());
        videojuego.setPlataforma(datosActualizados.getPlataforma());
        videojuego.setStock(datosActualizados.getStock());
        return videojuegoRepository.save(videojuego);
    }

    @Override
    public void eliminarVideojuego(Long videojuegoId) throws VideojuegoNotFoundException {
        Videojuego videojuego = obtenerVideojuegoPorId(videojuegoId);
        videojuegoRepository.delete(videojuego);
    }

    @Override
    public Videojuego agregarStock(Long videojuegoId, int cantidad) throws VideojuegoNotFoundException {
        Videojuego videojuego = obtenerVideojuegoPorId(videojuegoId);
        videojuego.setStock(videojuego.getStock() + cantidad);
        return videojuegoRepository.save(videojuego);
    }

    @Override
    public Videojuego disminuirStock(Long videojuegoId, int cantidad) throws VideojuegoNotFoundException {
        Videojuego videojuego = obtenerVideojuegoPorId(videojuegoId);
        if (videojuego.getStock() < cantidad) {
            throw new IllegalArgumentException("No hay suficiente stock disponible.");
        }
        videojuego.setStock(videojuego.getStock() - cantidad);
        return videojuegoRepository.save(videojuego);
    }    

    @Override
    public List<Videojuego> buscarPorTitulo(String titulo) {
        return videojuegoRepository.findByTitulo(titulo);
    }

    @Override
    public List<Videojuego> buscarPorPlataforma(String plataforma) {
        return videojuegoRepository.findByPlataforma(plataforma);
    }

    @Override
    public List<Videojuego> buscarPorRangoDePrecio(Double minPrecio, Double maxPrecio) {
        return videojuegoRepository.findByPrecioBetween(minPrecio, maxPrecio);
    }

    @Override
    public List<Videojuego> buscarPorDescripcion(String keyword) {
        return videojuegoRepository.findByDescripcionContaining(keyword);
    }

    public List<Videojuego> buscarPorCategoria(CategoriaJuego categoria) {
        return videojuegoRepository.findByCategoria(categoria);
    }

    @Override
    public Videojuego subirFoto(Long videojuegoId, MultipartFile foto) throws IOException, VideojuegoNotFoundException {
        Videojuego videojuego = obtenerVideojuegoPorId(videojuegoId);
        String fotoUrl = guardarFoto(foto);
        videojuego.setFotoUrl(fotoUrl);
        return videojuegoRepository.save(videojuego);
    }

    private String guardarFoto(MultipartFile foto) throws IOException {
    String folder = "fotos/";
    File directory = new File(folder);
    if (!directory.exists()) {
        directory.mkdirs(); // Crear el directorio si no existe
    }
    byte[] bytes = foto.getBytes();
    Path path = Paths.get(folder + foto.getOriginalFilename());
    Files.write(path, bytes);
    return path.toString();
}
}