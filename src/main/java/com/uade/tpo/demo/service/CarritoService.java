// CarritoService.java
package com.uade.tpo.demo.service;

import com.uade.tpo.demo.dao.CarritoDAO;
import com.uade.tpo.demo.dao.ItemCarritoDAO;
import com.uade.tpo.demo.entity.Carrito;
import com.uade.tpo.demo.entity.ItemCarrito;
import com.uade.tpo.demo.entity.Videojuego;
import com.uade.tpo.demo.exception.ResourceNotFoundException;

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
    private ItemCarritoDAO itemCarritoDAO;

    public Carrito getCarritoById(Long id) {
        return carritoDAO.findById(id).orElse(null);
    }

    public Carrito saveCarrito(Carrito carrito) {
        return carritoDAO.save(carrito);
    }

    public void addItemToCarrito(Long carritoId, Videojuego videojuego, Integer cantidad) {
        Carrito carrito = getCarritoById(carritoId);
        if (carrito != null) {
            ItemCarrito itemExistente = carrito.getItems().stream()
                .filter(item -> item.getVideojuego().getId().equals(videojuego.getId()))
                .findFirst()
                .orElse(null);

            if (itemExistente != null) {
                itemExistente.setCantidad(itemExistente.getCantidad() + cantidad);
                itemCarritoDAO.save(itemExistente);
            } else {
                ItemCarrito item = new ItemCarrito();
                item.setVideojuego(videojuego);
                item.setCantidad(cantidad);
                item.setCarrito(carrito);
                item.setTitulo(videojuego.getTitulo());
                item.setPrecio(videojuego.getPrecio());
                carrito.getItems().add(item);
                itemCarritoDAO.save(item);
            }
            carritoDAO.save(carrito);
        }
    }

    public void removeItemFromCarrito(Long itemId) {
        ItemCarrito item = itemCarritoDAO.findById(itemId).orElse(null);
        if (item != null) {
            if (item.getCantidad() > 1) {
                item.setCantidad(item.getCantidad() - 1);
                itemCarritoDAO.save(item);
                logger.info("Reduced quantity of item with ID {} in carrito with ID {}", itemId, item.getCarrito().getId());
            } else {
                Carrito carrito = item.getCarrito();
                carrito.getItems().remove(item);
                itemCarritoDAO.delete(item);
                carritoDAO.save(carrito);
                logger.info("Item with ID {} removed from carrito with ID {}", itemId, carrito.getId());
            }
        } else {
            logger.warn("Item with ID {} not found", itemId);
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
        Optional<Carrito> optionalCarrito = carritoDAO.findByUsuarioId(usuarioId);
        if (!optionalCarrito.isPresent()) {
            throw new ResourceNotFoundException("Carrito no encontrado para el usuario");
        }
        return optionalCarrito.get();
    }
}