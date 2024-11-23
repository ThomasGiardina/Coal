package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.controllers.config.JwtService;
import com.uade.tpo.demo.dto.ItemCarritoDTO;
import com.uade.tpo.demo.entity.Carrito;
import com.uade.tpo.demo.entity.ItemCarrito;
import com.uade.tpo.demo.entity.Pedido;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.Videojuego;
import com.uade.tpo.demo.service.CarritoService;
import com.uade.tpo.demo.service.PedidoService;
import com.uade.tpo.demo.service.VideojuegoService;

import java.util.Map;
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
    public ResponseEntity<?> addItemToCarrito(
            @PathVariable Long carritoId,
            @RequestHeader("Authorization") String token,
            @RequestBody ItemCarritoDTO itemCarritoDTO) {
        try {
            String jwt = token.substring(7); 
            String userEmail = jwtService.extractUsername(jwt);

            Usuario usuario = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            Carrito carrito = carritoService.getCarritoById(carritoId);
            if (carrito == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Carrito no encontrado");
            }

            if (carrito.getUsuario().getId() != usuario.getId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado al carrito");
            }

            Videojuego videojuego = videojuegoService.obtenerVideojuegoPorId(itemCarritoDTO.getVideojuegoId());
            if (videojuego == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Videojuego no encontrado");
            }

            carritoService.addItemToCarrito(carritoId, videojuego, itemCarritoDTO.getCantidad());

            return ResponseEntity.ok(Collections.singletonMap("message", "Item agregado al carrito"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Error interno del servidor"));
        }
    }

    @DeleteMapping("/{carritoId}/items/{itemId}")
    public ResponseEntity<?> removeItemFromCarrito(
            @PathVariable Long carritoId,
            @PathVariable Long itemId,
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7); 
            String userEmail = jwtService.extractUsername(jwt);

            Usuario usuario = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            Carrito carrito = carritoService.getCarritoById(carritoId);
            if (carrito == null || carrito.getUsuario().getId() != usuario.getId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes acceso a este carrito.");
            }

            carritoService.removeItemFromCarrito(itemId);

            return ResponseEntity.ok(Collections.singletonMap("message", "Item eliminado del carrito"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el producto");
        }
    }



    @PostMapping("/confirmar/{carritoId}")
    public ResponseEntity<Pedido> confirmarCarrito(
            @PathVariable Long carritoId,
            @RequestBody Map<String, Object> request
    ) {
        try {
            logger.info("Recibiendo solicitud para confirmar carrito con ID: {}", carritoId);

            String tipoEntrega = (String) request.get("tipoEntrega");
            Long metodoPagoId = request.get("metodoPagoId") != null
                    ? Long.valueOf(request.get("metodoPagoId").toString())
                    : null;
            String direccionEnvio = (String) request.get("direccionEnvio");

            Carrito carrito = carritoService.getCarritoById(carritoId);
            if (carrito == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            Usuario usuario = carrito.getUsuario();

            double montoTotal = carrito.getItems().stream()
                    .mapToDouble(item -> item.getCantidad() * item.getVideojuego().getPrecio())
                    .sum();
            int cantidadArticulos = carrito.getItems().stream()
                    .mapToInt(ItemCarrito::getCantidad)
                    .sum();

            String estadoPedido = Pedido.EstadoPedido.PENDIENTE.name(); 

            Pedido pedido = pedidoService.crearPedido(
                carritoId,
                tipoEntrega,
                metodoPagoId,
                direccionEnvio,
                usuario.getFirstName() + " " + usuario.getLastName(),
                montoTotal,
                cantidadArticulos,
                estadoPedido,
                usuario.getId()
            );

            logger.info("Pedido creado exitosamente con ID: {}", pedido.getId());
            return ResponseEntity.ok(pedido);

            

        } catch (Exception e) {
            logger.error("Error al confirmar carrito: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/usuarios/carrito")
    public ResponseEntity<?> getCarritoByUsuarioId(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7); 
            String userEmail = jwtService.extractUsername(jwt);

            Usuario usuario = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            Carrito carrito = carritoService.getCarritoByUsuarioId(usuario.getId());

            if (carrito == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Carrito no encontrado para este usuario.");
            }

            logger.info("Carrito encontrado: {}", carrito);
            logger.info("Ítems en el carrito: {}", carrito.getItems());

            return ResponseEntity.ok(carrito);

        } catch (Exception e) {
            logger.error("Error al obtener el carrito para el usuario", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener el carrito para el usuario.");
        }
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<?> updateItemQuantity(
            @PathVariable Long itemId,
            @RequestBody Map<String, Integer> payload) {
        try {
            int nuevaCantidad = payload.get("cantidad");
            carritoService.updateItemQuantityByItemId(itemId, nuevaCantidad);  
            return ResponseEntity.ok("Cantidad actualizada correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la cantidad");
        }
    }
}




