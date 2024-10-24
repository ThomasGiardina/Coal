package com.uade.tpo.demo.service;

import com.uade.tpo.demo.dao.CarritoDAO;
import com.uade.tpo.demo.dao.ItemCarritoDAO;
import com.uade.tpo.demo.entity.Carrito;
import com.uade.tpo.demo.entity.ItemCarrito;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.Videojuego;
import com.uade.tpo.demo.exception.ResourceNotFoundException;
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
                item.setPlataforma(videojuego.getPlataforma());
                carrito.getItems().add(item);
                itemCarritoDAO.save(item);
            }
            carritoDAO.save(carrito);
        }
    }

    public void removeItemFromCarrito(Long itemId) {
        ItemCarrito item = itemCarritoDAO.findById(itemId).orElse(null);
        if (item != null) {
            Carrito carrito = item.getCarrito();
            carrito.getItems().remove(item);
            itemCarritoDAO.delete(item);
            carritoDAO.save(carrito);
        } else {
            throw new ResourceNotFoundException("Item no encontrado");
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
            logger.info("No se encontró carrito para el usuario con ID: " + usuarioId + ", creando uno nuevo.");
            Carrito nuevoCarrito = new Carrito();
            
            Usuario usuario = userRepository.findById(usuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    
            nuevoCarrito.setUsuario(usuario);
    
            Carrito carritoGuardado = carritoDAO.save(nuevoCarrito);  
    
            logger.info("Nuevo carrito creado con ID: " + carritoGuardado.getId());
            return carritoGuardado;  
        }
        
        return optionalCarrito.get(); 
    }
    


    public void updateItemQuantityByItemId(Long itemId, int nuevaCantidad) {
        // Buscar el ítem directamente por su itemId
        ItemCarrito item = itemCarritoDAO.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado"));

        item.setCantidad(nuevaCantidad);
        itemCarritoDAO.save(item);  // Guardar los cambios
    }
    
    
    
    
}