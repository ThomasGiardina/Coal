package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.ItemPedido;
import com.uade.tpo.demo.entity.Videojuego;
import com.uade.tpo.demo.exception.VideojuegoNotFoundException;
import com.uade.tpo.demo.repository.VideojuegoRepository;
import com.uade.tpo.demo.repository.ItemPedidoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class VideojuegoServiceImpl implements VideojuegoService {

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

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
        List<ItemPedido> itemsPedido = itemPedidoRepository.findByVideojuegoId(videojuegoId);
        for (ItemPedido item : itemsPedido) {
            itemPedidoRepository.delete(item);
        }
        videojuegoRepository.delete(videojuego);
    }

    @Override
    public Videojuego subirFoto(Long videojuegoId, MultipartFile foto) throws IOException, VideojuegoNotFoundException {
        Videojuego videojuego = obtenerVideojuegoPorId(videojuegoId);
        videojuego.setFoto(foto.getBytes());  
        return videojuegoRepository.save(videojuego);
    }

    @Override
    public Videojuego subirCarruselImagen(Long videojuegoId, MultipartFile imagen, int index) throws IOException, VideojuegoNotFoundException {
        Videojuego videojuego = obtenerVideojuegoPorId(videojuegoId);

        switch (index) {
            case 1:
                videojuego.setCarruselImagen1(imagen.getBytes());
                break;
            case 2:
                videojuego.setCarruselImagen2(imagen.getBytes());
                break;
            case 3:
                videojuego.setCarruselImagen3(imagen.getBytes());
                break;
            default:
                throw new IllegalArgumentException("Índice de imagen del carrusel inválido: " + index);
        }

        return videojuegoRepository.save(videojuego);
    }
}
