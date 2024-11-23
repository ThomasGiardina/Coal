package com.uade.tpo.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.uade.tpo.demo.entity.ItemCarrito;

@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {
    @Query("SELECT i FROM ItemCarrito i JOIN FETCH i.videojuego WHERE i.carrito.id = :carritoId")
    List<ItemCarrito> findByCarritoIdWithVideojuegos(@Param("carritoId") Long carritoId);
}
