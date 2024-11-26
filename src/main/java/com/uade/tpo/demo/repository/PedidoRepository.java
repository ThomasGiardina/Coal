package com.uade.tpo.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.uade.tpo.demo.entity.Pedido;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long>{
    List<Pedido> findTop5ByEstadoPedidoOrderByFechaDesc(Pedido.EstadoPedido estadoPedido);


    @Query("SELECT p FROM Pedido p WHERE p.comprador.id = :usuarioId")
    List<Pedido> findByCompradorId(@Param("usuarioId") Long usuarioId);

    List<Pedido> findTop10ByEstadoPedidoOrderByFechaDesc(Pedido.EstadoPedido estadoPedido);

}
