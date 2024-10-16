package com.uade.tpo.demo.dao;

import com.uade.tpo.demo.entity.Carrito;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CarritoDAO extends JpaRepository<Carrito, Long> {
    Optional<Carrito> findByUsuarioId(Long usuarioId);
}
