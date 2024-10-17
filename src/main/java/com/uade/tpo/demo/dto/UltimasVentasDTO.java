package com.uade.tpo.demo.dto;

import com.uade.tpo.demo.entity.Pedido;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class UltimasVentasDTO {
    private Long id;
    private LocalDateTime fecha;
    private Double montoTotal;
    private List<ItemPedidoDTO> productosAdquiridos;

    @Data
    @AllArgsConstructor
    public static class ItemPedidoDTO {
        private String producto;
        private int cantidad;
    }
}