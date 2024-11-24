package com.uade.tpo.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.demo.dto.PedidoDTO;
import com.uade.tpo.demo.entity.MetodoPago;
import com.uade.tpo.demo.entity.Pedido;
import com.uade.tpo.demo.service.PedidoService;
import com.uade.tpo.demo.dto.ItemPedidoDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping("/{pedidoId}/pagar")
    public ResponseEntity<Pedido> pagarPedido(
        @PathVariable Long pedidoId,
        @RequestBody Map<String, String> request
    ) {
        Long metodoPagoId = Long.parseLong(request.get("metodoPagoId"));
        String cvv = request.get("cvv");

        if (cvv == null || cvv.isEmpty()) {
            throw new IllegalArgumentException("El CVV es obligatorio.");
        }

        Pedido pedido = pedidoService.pagarPedidoConValidacionCVV(pedidoId, metodoPagoId, cvv);
        return ResponseEntity.ok(pedido);
    }


    @PostMapping("/{pedidoId}/pagarUnico")
    public ResponseEntity<Pedido> pagarPedidoUnico(@PathVariable Long pedidoId, @RequestBody MetodoPago metodoPago) {
        Pedido pedido = pedidoService.pagarPedidoUnico(pedidoId, metodoPago);
        return ResponseEntity.ok(pedido);
    }

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> getAllPedidos() {
        List<PedidoDTO> pedidos = pedidoService.getAllPedidos();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PedidoDTO>> getPedidosByUsuario(@PathVariable Long usuarioId) {
        List<Pedido> pedidos = pedidoService.getPedidosByUsuarioId(usuarioId);
        List<PedidoDTO> pedidosDTO = pedidos.stream()
            .map(pedido -> PedidoDTO.builder()
                .id(pedido.getId())
                .fecha(pedido.getFecha().toLocalDate())
                .cliente(pedido.getNombreComprador())
                .tipoPago(pedido.getTipoPago().toString())
                .montoTotal(BigDecimal.valueOf(pedido.getMontoTotal()))
                .cantidadArticulos(pedido.getCantidadArticulos())
                .tipoEntrega(pedido.getTipoEntrega().toString())
                .estadoPedido(pedido.getEstadoPedido().toString())
                .productosAdquiridos(pedido.getProductosAdquiridos().stream()
                    .map(item -> ItemPedidoDTO.builder()
                        .titulo(item.getVideojuego().getTitulo())
                        .cantidad(item.getCantidad())
                        .build())
                    .collect(Collectors.toList()))
                .build())
            .collect(Collectors.toList());
        return ResponseEntity.ok(pedidosDTO);
    }




}