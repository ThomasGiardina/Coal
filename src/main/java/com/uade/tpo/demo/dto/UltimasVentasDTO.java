package com.uade.tpo.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class UltimasVentasDTO {
    private Long id;
    private LocalDateTime fecha;
    private Double montoTotal;
    private String estadoPedido;
    private List<ItemPedidoDTO> items;

    @Data
    @AllArgsConstructor
    @Builder
    public static class ItemPedidoDTO {
        private String titulo;
        private Integer cantidad;
    }
}