package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
    List<ItemPedido> findByVideojuegoId(Long videojuegoId);
}