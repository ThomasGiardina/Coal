package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.Favoritos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoritosRepository extends JpaRepository<Favoritos, Long> {
    List<Favoritos> findByUsuarioId(Long usuarioId);
    boolean existsByUsuarioIdAndVideojuegoId(Long usuarioId, Long videojuegoId);
    @Modifying
    @Query("DELETE FROM Favoritos f WHERE f.usuario.id = :usuarioId AND f.videojuego.id = :videojuegoId")
    void deleteByUsuarioIdAndVideojuegoId(@Param("usuarioId") Long usuarioId, @Param("videojuegoId") Long videojuegoId);
}
