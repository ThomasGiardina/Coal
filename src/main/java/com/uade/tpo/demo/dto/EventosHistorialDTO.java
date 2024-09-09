package com.uade.tpo.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventosHistorialDTO {
    private Long id;
    private Long usuarioId;
    private Long pedidoId;
    private Long historialId;
    private double precioTotal;
    private LocalDateTime fechaEvento;
    private List<String> nombreJuegos;
    private List<Double> precios;
}