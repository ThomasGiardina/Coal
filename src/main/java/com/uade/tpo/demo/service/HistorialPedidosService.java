package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.HistorialPedidos;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.repository.HistorialPedidosRepository;
import com.uade.tpo.demo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class HistorialPedidosService {

    @Autowired
    private HistorialPedidosRepository historialPedidosRepository;

    @Autowired
    private UserRepository userRepository;

    public HistorialPedidos obtenerHistorialPorUsuario(Long usuarioId) {
        return historialPedidosRepository.findByUsuarioId(usuarioId)
            .orElseThrow(() -> new RuntimeException("Historial no encontrado para el usuario con ID: " + usuarioId));
    }


    public HistorialPedidos obtenerHistorialPedidosPorId(Long historialId) {
        return historialPedidosRepository.findById(historialId).orElseThrow(() -> new RuntimeException("Historial no encontrado con ID: " + historialId));
    }

    public HistorialPedidos registrarNuevoHistorial(HistorialPedidos historial) {
        return historialPedidosRepository.save(historial);
    }


    public Long obtenerIdUsuarioPorEmail(String email) {
        Usuario usuario = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
        return usuario.getId();
    }
}