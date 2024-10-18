package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.controllers.config.JwtService;
import com.uade.tpo.demo.dto.ItemCarritoDTO;
import com.uade.tpo.demo.entity.Carrito;
import com.uade.tpo.demo.entity.Pedido;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.Videojuego;
import com.uade.tpo.demo.exception.InsufficientStockException;
import com.uade.tpo.demo.service.CarritoService;
import com.uade.tpo.demo.service.PedidoService;
import com.uade.tpo.demo.service.VideojuegoService;

import java.util.Map;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.uade.tpo.demo.repository.*;

import java.util.Collections;

@RestController
@RequestMapping("/carritos")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private VideojuegoService videojuegoService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    private static final Logger logger = LoggerFactory.getLogger(CarritoController.class);

    @GetMapping("/{id}")
    public Carrito getCarritoById(@PathVariable Long id) {
        return carritoService.getCarritoById(id);
    }

    @PostMapping("/{carritoId}/items")
    public ResponseEntity<?> addItemToCarrito(@PathVariable Long carritoId, @RequestHeader("Authorization") String token, @RequestBody ItemCarritoDTO itemCarritoDTO) {
        try {
            String jwt = token.substring(7); 
            String userEmail = jwtService.extractUsername(jwt);

            Usuario usuario = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            Carrito carrito = carritoService.getCarritoById(carritoId);
            if (carrito == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Carrito no encontrado");
            }

            Videojuego videojuego = videojuegoService.obtenerVideojuegoPorId(itemCarritoDTO.getVideojuegoId());
            if (videojuego == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Videojuego no encontrado");
            }

            carritoService.addItemToCarrito(carrito.getId(), videojuego, itemCarritoDTO.getCantidad());

            return ResponseEntity.ok(Collections.singletonMap("message", "Item agregado al carrito"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Error al agregar el item al carrito"));
        }
    }

    
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<?> removeItemFromCarrito(@PathVariable Long itemId) {
        try {
            carritoService.removeItemFromCarrito(itemId);
            return ResponseEntity.ok(Collections.singletonMap("message", "Item eliminado del carrito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el producto");
        }
    }



    @PostMapping("/confirmar/{carritoId}")
    public ResponseEntity<Pedido> confirmarCarrito(@PathVariable Long carritoId) {
        Carrito carrito = carritoService.getCarritoById(carritoId);

        if (carrito == null) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = carrito.getUsuario();
        Pedido pedido = pedidoService.crearPedido(carrito, usuario);

        carrito.getItems().forEach(item -> {
            Videojuego videojuego = item.getVideojuego();
            if (videojuego.getStock() < item.getCantidad()) {
                throw new InsufficientStockException("Stock insuficiente para el videojuego: " + videojuego.getTitulo());
            }
            videojuego.setStock(videojuego.getStock() - item.getCantidad());
            videojuegoService.actualizarVideojuego(videojuego.getId(), videojuego);
        });

        carritoService.vaciarCarrito(carrito); 

        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/usuarios/carrito")
    public ResponseEntity<?> getCarritoByUsuarioId(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7);
            String userEmail = jwtService.extractUsername(jwt);

            Usuario usuario = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            logger.info("ID del usuario autenticado: " + usuario.getId());

            Carrito carrito = carritoService.getCarritoByUsuarioId(usuario.getId());

            if (carrito == null) {
                logger.info("No se encontró el carrito para el usuario con ID: " + usuario.getId());
                return ResponseEntity.notFound().build();
            }

            List<ItemCarritoDTO> itemsDTO = carrito.getItems().stream().map(item -> {
                ItemCarritoDTO dto = new ItemCarritoDTO();
                dto.setId(item.getId());  
                dto.setCarritoId(carrito.getId());  
                dto.setTitulo(item.getTitulo());
                dto.setCantidad(item.getCantidad());
                dto.setPrecio(item.getPrecio());
                dto.setVideojuegoId(item.getVideojuego().getId());  
                dto.setPlataforma(item.getPlataforma());
                return dto;
            }).collect(Collectors.toList());


            return ResponseEntity.ok(itemsDTO);
        } catch (Exception e) {
            logger.error("Error obteniendo el carrito para el usuario", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @PutMapping("/{carritoId}/items/{itemId}")
    public ResponseEntity<?> updateItemQuantity(
            @PathVariable Long carritoId,
            @PathVariable Long itemId,
            @RequestBody Map<String, Integer> payload) {
        try {
            int nuevaCantidad = payload.get("cantidad");
            carritoService.updateItemQuantity(carritoId, itemId, nuevaCantidad);  
            return ResponseEntity.ok("Cantidad actualizada correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la cantidad");
        }
    }
}




