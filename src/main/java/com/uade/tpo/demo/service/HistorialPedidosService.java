package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.HistorialPedidos;
import com.uade.tpo.demo.repository.HistorialPedidosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class HistorialPedidosService {

    @Autowired
    private HistorialPedidosRepository historialPedidosRepository;

    public HistorialPedidos obtenerHistorialPorUsuario(Long usuarioId) {
        return historialPedidosRepository.findByUsuarioId(usuarioId).orElseThrow(() -> new RuntimeException("Historial no encontrado para el usuario con ID: " + usuarioId));
    }

    public HistorialPedidos obtenerHistorialPedidosPorId(Long historialId) {
        return historialPedidosRepository.findById(historialId).orElseThrow(() -> new RuntimeException("Historial no encontrado con ID: " + historialId));
    }

    public HistorialPedidos registrarNuevoHistorial(HistorialPedidos historial) {
        return historialPedidosRepository.save(historial);
    }
}