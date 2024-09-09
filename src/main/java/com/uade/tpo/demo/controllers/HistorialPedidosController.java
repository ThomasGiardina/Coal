package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.dto.HistorialPedidosDTO;
import com.uade.tpo.demo.dto.EventosHistorialDTO;
import com.uade.tpo.demo.entity.HistorialPedidos;
import com.uade.tpo.demo.entity.ItemPedido;
import com.uade.tpo.demo.entity.EventosHistorial;
import com.uade.tpo.demo.service.HistorialPedidosService;
import com.uade.tpo.demo.service.EventosHistorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/historial")
public class HistorialPedidosController {

    @Autowired
    private HistorialPedidosService historialPedidosService;

    @Autowired
    private EventosHistorialService eventosHistorialService;

    @GetMapping("/{historialId}")
    public ResponseEntity<HistorialPedidosDTO> obtenerHistorialPorId(@PathVariable Long historialId) {
        HistorialPedidos historial = historialPedidosService.obtenerHistorialPedidosPorId(historialId);
        HistorialPedidosDTO historialDTO = convertirAHistorialPedidosDTO(historial);
        return ResponseEntity.ok(historialDTO);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<HistorialPedidosDTO> obtenerHistorialPorUsuario(@PathVariable Long usuarioId) {
        HistorialPedidos historial = historialPedidosService.obtenerHistorialPorUsuario(usuarioId);
        
        HistorialPedidosDTO historialDTO = convertirAHistorialPedidosDTO(historial);
        
        return ResponseEntity.ok(historialDTO);
    }

    @GetMapping("/{historialId}/eventos")
    public ResponseEntity<List<EventosHistorialDTO>> obtenerEventosPorHistorialId(@PathVariable Long historialId) {
        List<EventosHistorial> eventos = eventosHistorialService.obtenerEventosPorHistorialId(historialId);
        List<EventosHistorialDTO> eventosDTOs = eventos.stream()
            .map(this::convertirAEventosHistorialDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(eventosDTOs);
    }

    private HistorialPedidosDTO convertirAHistorialPedidosDTO(HistorialPedidos historial) {
        HistorialPedidosDTO dto = new HistorialPedidosDTO();
        dto.setId(historial.getId());
        dto.setUsuarioId(historial.getUsuario() != null ? historial.getUsuario().getId() : null);
        return dto;
    }


    private EventosHistorialDTO convertirAEventosHistorialDTO(EventosHistorial evento) {
        EventosHistorialDTO dto = new EventosHistorialDTO();
        
        dto.setId(evento.getId());
        dto.setPedidoId(evento.getPedido().getId());
        dto.setHistorialId(evento.getHistorial().getId());
        dto.setPrecioTotal(evento.getPrecioTotal());
        dto.setFechaEvento(evento.getFechaEvento());
    
        List<String> nombres = evento.getItems().stream()
            .map(item -> item.getVideojuego().getTitulo())  
            .collect(Collectors.toList());
    
        List<Double> precios = evento.getItems().stream()
            .map(ItemPedido::getPrecio)  
            .collect(Collectors.toList());
    
        dto.setNombreJuegos(nombres);
        dto.setPrecios(precios);
    
        return dto;
    }
}