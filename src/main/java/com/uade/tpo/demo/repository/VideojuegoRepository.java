package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.Videojuego;
import com.uade.tpo.demo.entity.Videojuego.CategoriaJuego;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideojuegoRepository extends JpaRepository<Videojuego, Long> {

    List<Videojuego> findByTitulo(String titulo);

    List<Videojuego> findByPlataforma(String plataforma);

    List<Videojuego> findByStockLessThan(Integer stock);

    @Query("SELECT v FROM Videojuego v WHERE v.precio BETWEEN ?1 AND ?2")
    List<Videojuego> findByPrecioBetween(Double minPrecio, Double maxPrecio);

    @Query("SELECT v FROM Videojuego v WHERE v.descripcion LIKE %?1%")
    List<Videojuego> findByDescripcionContaining(String keyword);

    List<Videojuego> findByCategoria(CategoriaJuego categoria);
}
