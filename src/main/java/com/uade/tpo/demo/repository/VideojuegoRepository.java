package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.Videojuego;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideojuegoRepository extends JpaRepository<Videojuego, Long> {
    List<Videojuego> findByStockLessThan(Integer stock);
    List<Videojuego> findTop10ByOrderByVentasDesc(); 
    List<Videojuego> findTop5ByOrderByVentasDesc();
    Optional<Videojuego> findByTitulo(String titulo);
}
