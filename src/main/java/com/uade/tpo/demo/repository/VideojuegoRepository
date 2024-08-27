package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.Videojuego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideojuegoRepository extends JpaRepository<Videojuego, Long> {

    // Método para encontrar videojuegos por título
    List<Videojuego> findByTitulo(String titulo);

    // Método para encontrar videojuegos por plataforma
    List<Videojuego> findByPlataforma(String plataforma);

    // Método para encontrar videojuegos cuyo stock esté por debajo de un cierto umbral
    List<Videojuego> findByStockLessThan(Integer stock);

    // Método personalizado usando JPQL para buscar videojuegos por un rango de precio
    @Query("SELECT v FROM Videojuego v WHERE v.precio BETWEEN ?1 AND ?2")
    List<Videojuego> findByPrecioBetween(Double minPrecio, Double maxPrecio);

    // Método personalizado para encontrar videojuegos que contienen una cadena en su descripción
    @Query("SELECT v FROM Videojuego v WHERE v.descripcion LIKE %?1%")
    List<Videojuego> findByDescripcionContaining(String keyword);
}
