package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.EventosHistorial;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventosHistorialRepository extends JpaRepository<EventosHistorial, Long> {
    List<EventosHistorial> findByHistorialId(Long historialId);
}