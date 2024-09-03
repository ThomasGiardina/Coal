package com.uade.tpo.demo.service;

import com.uade.tpo.demo.dao.CarritoDAO;
import com.uade.tpo.demo.dao.ItemCarritoDAO;
import com.uade.tpo.demo.entity.Carrito;
import com.uade.tpo.demo.entity.ItemCarrito;
import com.uade.tpo.demo.entity.Videojuego;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarritoService {
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
            // Buscar si el item ya existe en el carrito
            ItemCarrito itemExistente = carrito.getItems().stream()
                .filter(item -> item.getVideojuego().getId().equals(videojuego.getId()))
                .findFirst()
                .orElse(null);

            if (itemExistente != null) {
                // Si el item ya existe, actualizar la cantidad
                itemExistente.setCantidad(itemExistente.getCantidad() + cantidad);
                itemCarritoDAO.save(itemExistente);
            } else {
                // Si el item no existe, crear un nuevo item
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
        itemCarritoDAO.deleteById(itemId);
    }

    public void vaciarCarrito(Carrito carrito) {
        carrito.getItems().clear();
        carritoDAO.save(carrito);
    }
}