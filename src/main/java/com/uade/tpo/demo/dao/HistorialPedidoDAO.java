package com.uade.tpo.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.entity.HistorialPedidos;


@Repository
public interface HistorialPedidoDAO extends JpaRepository<HistorialPedidos, Long> {

    
} 
