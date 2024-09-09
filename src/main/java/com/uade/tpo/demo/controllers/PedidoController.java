package com.uade.tpo.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.demo.entity.MetodoPago;
import com.uade.tpo.demo.entity.Pedido;
import com.uade.tpo.demo.service.PedidoService;

import java.util.Map; // Importación necesaria

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping("/{pedidoId}/pagar")
    public ResponseEntity<Pedido> pagarPedido(@PathVariable Long pedidoId, @RequestBody Map<String, Long> request) {
        Long metodoPagoId = request.get("metodoPagoId");
        System.out.println("Método de pago ID: " + metodoPagoId); // Log para verificar
        Pedido pedido = pedidoService.pagarPedido(pedidoId, metodoPagoId);
        return ResponseEntity.ok(pedido);
    }

    @PostMapping("/{pedidoId}/pagarUnico")
    public ResponseEntity<Pedido> pagarPedidoUnico(@PathVariable Long pedidoId, @RequestBody MetodoPago metodoPago) {
        Pedido pedido = pedidoService.pagarPedidoUnico(pedidoId, metodoPago);
        return ResponseEntity.ok(pedido);
    }
}