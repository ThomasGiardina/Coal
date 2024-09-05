package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.MetodoPago;
import com.uade.tpo.demo.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {
    List<MetodoPago> findAllByUsuario(Usuario usuario);
}