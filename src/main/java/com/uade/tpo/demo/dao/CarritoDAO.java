package com.uade.tpo.demo.dao;

import com.uade.tpo.demo.entity.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarritoDAO extends JpaRepository<Carrito, Long> {
}