package com.uade.tpo.demo.dao;

import com.uade.tpo.demo.entity.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemCarritoDAO extends JpaRepository<ItemCarrito, Long> {
}