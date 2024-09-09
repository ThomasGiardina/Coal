package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.HistorialPedidos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistorialPedidosRepository extends JpaRepository<HistorialPedidos, Long> {
    Optional<HistorialPedidos> findByUsuarioId(Long usuarioId);
}