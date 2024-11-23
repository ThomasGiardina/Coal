package com.uade.tpo.demo.dao;

import com.uade.tpo.demo.entity.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemCarritoDAO extends JpaRepository<ItemCarrito, Long> {
    Optional<ItemCarrito> findByCarritoIdAndVideojuegoId(Long carritoId, Long videojuegoId);
    List<ItemCarrito> findByCarritoId(Long carritoId);
}