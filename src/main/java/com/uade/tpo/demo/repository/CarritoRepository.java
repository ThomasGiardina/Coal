package com.uade.tpo.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.entity.Carrito;

import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    Optional<Carrito> findByUsuarioId(Long usuarioId);

    @Query("SELECT c FROM Carrito c " +
       "LEFT JOIN FETCH c.items i " +
       "LEFT JOIN FETCH i.videojuego v " +
       "WHERE c.usuario.id = :usuarioId")
Optional<Carrito> findCarritoByUsuarioIdWithItems(@Param("usuarioId") Long usuarioId);

}
