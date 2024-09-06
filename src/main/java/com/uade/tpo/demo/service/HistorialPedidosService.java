package com.uade.tpo.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uade.tpo.demo.entity.HistorialPedidos;
import com.uade.tpo.demo.repository.HistorialPedidosRepository;
import jakarta.transaction.Transactional;

@Service
public class HistorialPedidosService {

    @Autowired
    private HistorialPedidosRepository historialPedidosRepository;

    @Transactional
    public HistorialPedidos saveHistorial(HistorialPedidos historial) {
        return historialPedidosRepository.save(historial);
    }

    @Transactional
    public HistorialPedidos getHistorialPorId(Long id) {
        return historialPedidosRepository.findById(id).orElse(null);
    }

    @Transactional
    public List<HistorialPedidos> getHistorialPorUsuarioId(Long idUsuario) {
        return historialPedidosRepository.findByUsuario_Id(idUsuario);
    }
}
