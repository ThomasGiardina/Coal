package com.uade.tpo.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.entity.HistorialPedidos;

@Repository
public interface HistorialPedidosRepository extends JpaRepository<HistorialPedidos, Long> {
    List<HistorialPedidos> findByUsuario_Id(Long idUsuario);
}