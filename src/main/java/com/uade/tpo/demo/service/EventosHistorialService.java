package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.EventosHistorial;
import com.uade.tpo.demo.repository.EventosHistorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventosHistorialService {

    @Autowired
    private EventosHistorialRepository eventosHistorialRepository;

    public List<EventosHistorial> obtenerEventosPorHistorialId(Long historialId) {
        return eventosHistorialRepository.findByHistorialId(historialId);
    }

    public EventosHistorial registrarEvento(EventosHistorial evento) {
        return eventosHistorialRepository.save(evento);
    }

    public List<EventosHistorial> obtenerTodosLosEventos() {
        return eventosHistorialRepository.findAll();  
    }
}