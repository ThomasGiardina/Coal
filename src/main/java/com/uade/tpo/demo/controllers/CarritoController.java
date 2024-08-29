package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.entity.Carrito;
import com.uade.tpo.demo.entity.Videojuego;
import com.uade.tpo.demo.service.CarritoService;
import com.uade.tpo.demo.service.VideojuegoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carritos")
public class CarritoController {
    @Autowired
    private CarritoService carritoService;

    @Autowired
    private VideojuegoService videojuegoService;

    @GetMapping("/{id}")
    public Carrito getCarritoById(@PathVariable Long id) {
        return carritoService.getCarritoById(id);
    }

    @PostMapping
    public Carrito saveCarrito(@RequestBody Carrito carrito) {
        return carritoService.saveCarrito(carrito);
    }

    @PostMapping("/{carritoId}/items")
    public void addItemToCarrito(@PathVariable Long carritoId, @RequestParam Long videojuegoId, @RequestParam Integer cantidad) {
        Videojuego videojuego = videojuegoService.obtenerVideojuegoPorId(videojuegoId);
        carritoService.addItemToCarrito(carritoId, videojuego, cantidad);
    }

    @DeleteMapping("/items/{itemId}")
    public void removeItemFromCarrito(@PathVariable Long itemId) {
        carritoService.removeItemFromCarrito(itemId);
    }
}