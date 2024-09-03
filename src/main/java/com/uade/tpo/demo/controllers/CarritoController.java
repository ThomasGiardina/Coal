package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.dto.ItemCarritoDTO;
import com.uade.tpo.demo.entity.Carrito;
import com.uade.tpo.demo.entity.Pedido;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.Videojuego;
import com.uade.tpo.demo.service.CarritoService;
import com.uade.tpo.demo.service.PedidoService;
import com.uade.tpo.demo.service.VideojuegoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carritos")
public class CarritoController {
    @Autowired
    private CarritoService carritoService;

    @Autowired
    private VideojuegoService videojuegoService;

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/{id}")
    public Carrito getCarritoById(@PathVariable Long id) {
        return carritoService.getCarritoById(id);
    }

    @PostMapping
    public Carrito saveCarrito(@RequestBody Carrito carrito) {
        return carritoService.saveCarrito(carrito);
    }

    @PostMapping("/{carritoId}/items")
    public void addItemToCarrito(@PathVariable Long carritoId, @RequestBody ItemCarritoDTO itemCarritoDTO) {
        Videojuego videojuego = videojuegoService.obtenerVideojuegoPorId(itemCarritoDTO.getVideojuegoId());
        carritoService.addItemToCarrito(carritoId, videojuego, itemCarritoDTO.getCantidad());
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
        carritoService.vaciarCarrito(carrito);

        return ResponseEntity.ok(pedido);
    }
}