package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.Favoritos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritosRepository extends JpaRepository<Favoritos, Long> {
    List<Favoritos> findByUsuarioId(Long usuarioId);
    Optional<Favoritos> findByUsuarioIdAndVideojuegoId(Long usuarioId, Long videojuegoId);
    void deleteByUsuarioIdAndVideojuegoId(Long usuarioId, Long videojuegoId);
}
