package com.uade.tpo.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.demo.entity.MetodoPago;
import com.uade.tpo.demo.service.MetodoPagoService;

import java.util.List;

@RestController
@RequestMapping("/api/metodos-pago")
public class MetodoPagoController {

    @Autowired
    private MetodoPagoService metodoPagoService;

    @GetMapping
    public List<MetodoPago> getAllMetodosPago() {
        return metodoPagoService.getAllMetodosPago();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetodoPago> getMetodoPagoById(@PathVariable Long id) {
        MetodoPago metodoPago = metodoPagoService.getMetodoPagoById(id);
        return ResponseEntity.ok(metodoPago);
    }

    @PostMapping
    public MetodoPago createMetodoPago(@RequestBody MetodoPago metodoPago) {
        return metodoPagoService.saveMetodoPago(metodoPago);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetodoPago> updateMetodoPago(@PathVariable Long id, @RequestBody MetodoPago metodoPagoDetails) {
        MetodoPago updatedMetodoPago = metodoPagoService.updateMetodoPago(id, metodoPagoDetails);
        return ResponseEntity.ok(updatedMetodoPago);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMetodoPago(@PathVariable Long id) {
        metodoPagoService.deleteMetodoPago(id);
        return ResponseEntity.noContent().build();
    }
}