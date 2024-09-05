package com.uade.tpo.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.demo.entity.Pedido;
import com.uade.tpo.demo.service.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping("/{pedidoId}/pagar")
    public ResponseEntity<Pedido> pagarPedido(@PathVariable Long pedidoId, @RequestBody Long metodoPagoId) {
        Pedido pedido = pedidoService.pagarPedido(pedidoId, metodoPagoId);
        return ResponseEntity.ok(pedido);
    }
}