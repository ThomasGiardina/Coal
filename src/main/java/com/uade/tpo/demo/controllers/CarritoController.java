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
            // Extraer el usuario desde el token JWT
            String jwt = token.substring(7); // Ignorar "Bearer "
            String userEmail = jwtService.extractUsername(jwt);

            // Obtener el usuario
            Usuario usuario = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            // Obtener el carrito del usuario
            Carrito carrito = carritoService.getCarritoById(carritoId);
            if (carrito == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Carrito no encontrado");
            }

            // Obtenemos el videojuego
            Videojuego videojuego = videojuegoService.obtenerVideojuegoPorId(itemCarritoDTO.getVideojuegoId());
            if (videojuego == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Videojuego no encontrado");
            }

            // Agregar el videojuego al carrito del usuario
            carritoService.addItemToCarrito(carrito.getId(), videojuego, itemCarritoDTO.getCantidad());

            // Aquí devolvemos un JSON válido
            return ResponseEntity.ok(Collections.singletonMap("message", "Item agregado al carrito"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Error al agregar el item al carrito"));
        }
    }

    @DeleteMapping("/items/{itemId}")
    public void removeItemFromCarrito(@PathVariable Long itemId) {
        carritoService.removeItemFromCarrito(itemId);
    }

    @PostMapping("/confirmar/{carritoId}")
    public ResponseEntity<Pedido> confirmarCarrito(@PathVariable Long carritoId) {
        Carrito carrito = carritoService.getCarritoById(carritoId);

        if (carrito == null) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = carrito.getUsuario();
        Pedido pedido = pedidoService.crearPedido(carrito, usuario);

        // Restar la cantidad de videojuegos del stock
        carrito.getItems().forEach(item -> {
            Videojuego videojuego = item.getVideojuego();
            if (videojuego.getStock() < item.getCantidad()) {
                throw new InsufficientStockException("Stock insuficiente para el videojuego: " + videojuego.getTitulo());
            }
            videojuego.setStock(videojuego.getStock() - item.getCantidad());
            videojuegoService.actualizarVideojuego(videojuego.getId(), videojuego);
        });

        carritoService.vaciarCarrito(carrito); // Vaciar el carrito después de confirmar la compra

        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/usuarios/carrito")
    public ResponseEntity<?> getCarritoByUsuarioId(@RequestHeader("Authorization") String token) {
        try {
            // Extraer el token JWT y obtener el email del usuario
            String jwt = token.substring(7);
            String userEmail = jwtService.extractUsername(jwt);

            // Obtener el usuario por su email
            Usuario usuario = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            // Imprimir el ID del usuario
            logger.info("ID del usuario autenticado: " + usuario.getId());

            // Buscar el carrito del usuario
            Carrito carrito = carritoService.getCarritoByUsuarioId(usuario.getId());

            if (carrito == null) {
                logger.info("No se encontró el carrito para el usuario con ID: " + usuario.getId());
                return ResponseEntity.notFound().build();
            }

            // Crear una lista de ItemCarritoDTO con todos los detalles de los ítems
            List<ItemCarritoDTO> itemsDTO = carrito.getItems().stream().map(item -> {
                ItemCarritoDTO dto = new ItemCarritoDTO();
                dto.setId(item.getId());  // Incluir el ID del ítem
                dto.setCarritoId(carrito.getId());  // ID del carrito
                dto.setTitulo(item.getTitulo());
                dto.setCantidad(item.getCantidad());
                dto.setPrecio(item.getPrecio());
                dto.setVideojuegoId(item.getVideojuego().getId());  // ID del videojuego
                dto.setPlataforma(item.getPlataforma());
                return dto;
            }).collect(Collectors.toList());

            // Retornar la respuesta con los detalles del carrito
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
            carritoService.updateItemQuantity(carritoId, itemId, nuevaCantidad);  // Verifica que este método exista y funcione
            return ResponseEntity.ok("Cantidad actualizada correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la cantidad");
        }
    }
}




