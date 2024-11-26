package com.uade.tpo.demo.service;

import com.uade.tpo.demo.dao.CarritoDAO;
import com.uade.tpo.demo.dao.ItemCarritoDAO;
import com.uade.tpo.demo.entity.Carrito;
import com.uade.tpo.demo.entity.ItemCarrito;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.Videojuego;
import com.uade.tpo.demo.exception.ResourceNotFoundException;
import com.uade.tpo.demo.repository.ItemCarritoRepository;
import com.uade.tpo.demo.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CarritoService {
    private static final Logger logger = LoggerFactory.getLogger(CarritoService.class);
    @Autowired
    private CarritoDAO carritoDAO;

    @Autowired
    private ItemCarritoRepository itemCarritoRepository;

    @Autowired
    private ItemCarritoDAO itemCarritoDAO;

    @Autowired
    private UserRepository userRepository;

    public Carrito getCarritoById(Long id) {
        return carritoDAO.findById(id).orElse(null);
    }

    public Carrito saveCarrito(Carrito carrito) {
        return carritoDAO.save(carrito);
    }

    public void addItemToCarrito(Long carritoId, Videojuego videojuego, Integer cantidad) {
        Carrito carrito = getCarritoById(carritoId);
        if (carrito == null) {
            throw new ResourceNotFoundException("Carrito no encontrado");
        }
    
        Optional<ItemCarrito> itemExistente = itemCarritoDAO.findByCarritoIdAndVideojuegoId(carritoId, videojuego.getId());
        if (itemExistente.isPresent()) {
            ItemCarrito item = itemExistente.get();
            int nuevaCantidad = item.getCantidad() + cantidad;
    
            if (nuevaCantidad > videojuego.getStock()) {
                throw new IllegalArgumentException("Stock insuficiente para agregar al carrito. Stock disponible: " + videojuego.getStock());
            }
    
            item.setCantidad(nuevaCantidad);
            itemCarritoDAO.save(item);
            logger.info("Actualizado item existente en el carrito: " + item.getId() + " con cantidad: " + nuevaCantidad);
        } else {
            if (cantidad > videojuego.getStock()) {
                throw new IllegalArgumentException("Stock insuficiente para agregar al carrito. Stock disponible: " + videojuego.getStock());
            }
    
            ItemCarrito nuevoItem = new ItemCarrito();
            nuevoItem.setVideojuego(videojuego);
            nuevoItem.setCantidad(cantidad);
            nuevoItem.setCarrito(carrito);
            nuevoItem.setTitulo(videojuego.getTitulo());
            nuevoItem.setPrecio(videojuego.getPrecio());
            nuevoItem.setPlataforma(videojuego.getPlataforma());
            itemCarritoDAO.save(nuevoItem);
            logger.info("Nuevo item agregado al carrito: " + nuevoItem.getId());
        }
    }    

    public void removeItemFromCarrito(Long itemId) {
        Optional<ItemCarrito> itemCarrito = itemCarritoRepository.findById(itemId);
        if (itemCarrito.isPresent()) {
            itemCarritoRepository.delete(itemCarrito.get());
        } else {
            throw new IllegalArgumentException("Item no encontrado en el carrito.");
        }
    }
    
    public void vaciarCarrito(Carrito carrito) {
        for (ItemCarrito item : carrito.getItems()) {
            itemCarritoDAO.delete(item);
        }
        carrito.getItems().clear();
        carritoDAO.save(carrito);
    }
    

    public Carrito getCarritoByUsuarioId(Long usuarioId) {
        Optional<Carrito> optionalCarrito = carritoDAO.findCarritoByUsuarioIdWithItems(usuarioId);
    
        if (optionalCarrito.isEmpty()) {
            logger.info("No se encontró carrito para el usuario con ID: " + usuarioId + ", creando uno nuevo.");
            Carrito nuevoCarrito = new Carrito();
    
            Usuario usuario = userRepository.findById(usuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    
            nuevoCarrito.setUsuario(usuario);
            return carritoDAO.save(nuevoCarrito);
        }
    
        return optionalCarrito.get();
    }
    

    public void updateItemQuantityByItemId(Long itemId, int nuevaCantidad) {
        ItemCarrito item = itemCarritoDAO.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado"));

        item.setCantidad(nuevaCantidad);
        itemCarritoDAO.save(item);
    }
    
    public ItemCarrito getItemByCarritoAndVideojuego(Long carritoId, Long videojuegoId) {
        return itemCarritoDAO.findByCarritoIdAndVideojuegoId(carritoId, videojuegoId)
                .orElse(null);
    }
    
    
}