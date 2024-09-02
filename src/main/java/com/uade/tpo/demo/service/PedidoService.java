package com.uade.tpo.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.tpo.demo.entity.Carrito;
import com.uade.tpo.demo.entity.ItemPedido;
import com.uade.tpo.demo.entity.Pedido;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.Pedido.EstadoPedido;
import com.uade.tpo.demo.repository.PedidoRepository;

import jakarta.transaction.Transactional;

@Service
public class PedidoService {
    
    @Autowired
    private PedidoRepository pedidoRepository;

    @Transactional
    public Pedido crearPedido(Carrito carrito, Usuario usuario) {
        Pedido pedido = new Pedido();
        pedido.setComprador(usuario);
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.PENDIENTE);

        List<ItemPedido> items = carrito.getItems().stream()
            .map(itemCarrito -> {
                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setVideojuego(itemCarrito.getVideojuego());
                itemPedido.setCantidad(itemCarrito.getCantidad());
                itemPedido.setPrecio(itemCarrito.getPrecio());
                return itemPedido;
            }).collect(Collectors.toList());

        pedido.setProductosAdquiridos(items);
        pedido.setMontoTotal(carrito.getItems().stream()
            .mapToDouble(item -> item.getPrecio() * item.getCantidad()).sum());

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido pagarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        if (pedido.getEstado() == EstadoPedido.PENDIENTE) {
            pedido.setEstado(EstadoPedido.CONFIRMADO);
            return pedidoRepository.save(pedido);
        } else {
            throw new RuntimeException("El pedido no está en estado pendiente");
        }
    }

    @Transactional
    public void cancelarPedidosPendientes() {
        List<Pedido> pedidosPendientes = pedidoRepository.findAll().stream()
            .filter(pedido -> pedido.getEstado() == EstadoPedido.PENDIENTE && pedido.getFecha().isBefore(LocalDateTime.now().minusHours(1)))
            .collect(Collectors.toList());

        for (Pedido pedido : pedidosPendientes) {
            pedido.setEstado(EstadoPedido.CANCELADO);
            pedidoRepository.save(pedido);
        }
    }
}