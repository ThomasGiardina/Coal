package com.uade.tpo.demo.dao;

import com.uade.tpo.demo.entity.Videojuego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideojuegoDAO extends JpaRepository<Videojuego, Long> {
    List<Videojuego> findByTitulo(String titulo);
}
