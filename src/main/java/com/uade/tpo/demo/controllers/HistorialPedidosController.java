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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/historial")
public class HistorialPedidosController {

    @Autowired
    private HistorialPedidosService historialPedidosService;

    @Autowired
    private EventosHistorialService eventosHistorialService;

    @GetMapping("/usuario")
    public ResponseEntity<List<EventosHistorialDTO>> obtenerHistorialPorUsuario() {
        // Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogueado = authentication.getName();  // Se asume que el username es el email

        // Verificar si el usuario autenticado tiene el rol de ADMIN
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        List<EventosHistorialDTO> eventosDTOs;

        if (esAdmin) {
            // Si el usuario es admin, obtener todos los eventos de todos los historiales
            List<EventosHistorial> eventos = eventosHistorialService.obtenerTodosLosEventos();
            eventosDTOs = eventos.stream()
                .map(this::convertirAEventosHistorialDTO)
                .collect(Collectors.toList());
        } else {
            // Si el usuario no es admin, obtener el historial solo del usuario logueado
            Long usuarioId = historialPedidosService.obtenerIdUsuarioPorEmail(emailUsuarioLogueado);
            HistorialPedidos historial = historialPedidosService.obtenerHistorialPorUsuario(usuarioId);
            List<EventosHistorial> eventos = eventosHistorialService.obtenerEventosPorHistorialId(historial.getId());
            eventosDTOs = eventos.stream()
                .map(this::convertirAEventosHistorialDTO)
                .collect(Collectors.toList());
        }

        return ResponseEntity.ok(eventosDTOs);
    }

    @GetMapping("/{historialId}")
    public ResponseEntity<HistorialPedidosDTO> obtenerHistorialPorId(@PathVariable Long historialId) {
        HistorialPedidos historial = historialPedidosService.obtenerHistorialPedidosPorId(historialId);
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