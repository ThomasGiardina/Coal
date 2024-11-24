package com.uade.tpo.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {
    private Long id;
    private LocalDate fecha;
    private String cliente;
    private String tipoPago;
    private BigDecimal montoTotal;
    private int cantidadArticulos;
    private String tipoEntrega;
    private String estadoPedido;
    private List<ItemPedidoDTO> productosAdquiridos; 
}
