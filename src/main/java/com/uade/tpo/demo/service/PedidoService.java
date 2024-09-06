package com.uade.tpo.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.tpo.demo.entity.Carrito;
import com.uade.tpo.demo.entity.HistorialPedidos;
import com.uade.tpo.demo.entity.ItemPedido;
import com.uade.tpo.demo.entity.MetodoPago;
import com.uade.tpo.demo.entity.Pedido;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.Videojuego;
import com.uade.tpo.demo.exception.InsufficientStockException;
import com.uade.tpo.demo.entity.Pedido.EstadoPedido;
import com.uade.tpo.demo.repository.PedidoRepository;
import com.uade.tpo.demo.repository.HistorialPedidosRepository;
import com.uade.tpo.demo.repository.MetodoPagoRepository;

import jakarta.transaction.Transactional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private HistorialPedidosRepository historialPedidosRepository;

    @Autowired
    private MetodoPagoRepository metodoPagoRepository;

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private VideojuegoService videojuegoService;

    @Transactional
    public Pedido crearPedido(Carrito carrito, Usuario usuario) {
        Pedido pedido = new Pedido();
        pedido.setComprador(usuario);
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado(Pedido.EstadoPedido.PENDIENTE);

        // Verificar el stock de cada videojuego
        carrito.getItems().forEach(itemCarrito -> {
            Videojuego videojuego = itemCarrito.getVideojuego();
            if (videojuego.getStock() < itemCarrito.getCantidad()) {
                throw new InsufficientStockException("No hay suficiente stock para el videojuego: " + videojuego.getTitulo());
            }
        });

        // Disminuir el stock si hay suficiente
        carrito.getItems().forEach(itemCarrito -> {
            Videojuego videojuego = itemCarrito.getVideojuego();
            videojuegoService.disminuirStock(videojuego.getId(), itemCarrito.getCantidad());
        });

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

        Pedido nuevoPedido = pedidoRepository.save(pedido);
        carritoService.vaciarCarrito(carrito);

        return nuevoPedido;
    }

    @Transactional
    public Pedido pagarPedido(Long pedidoId, Long metodoPagoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new RuntimeException("El pedido no está en estado pendiente");
        }

        if (metodoPagoId == null) {
            throw new RuntimeException("Debe seleccionar un método de pago antes de proceder con el pago.");
        }

        // Obtener el método de pago seleccionado y asignarlo al pedido
        MetodoPago metodoPago = metodoPagoRepository.findById(metodoPagoId)
            .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

        pedido.setMetodoPago(metodoPago);
        pedido.setEstado(EstadoPedido.CONFIRMADO);
        pedidoRepository.save(pedido);

        // Registrar cada videojuego en el historial
        for (ItemPedido item : pedido.getProductosAdquiridos()) {
            HistorialPedidos historial = new HistorialPedidos();
            historial.setPedido(pedido);
            historial.setUsuario(pedido.getComprador());
            historial.setVideojuego(item.getVideojuego());
            historial.setCantidad(item.getCantidad());
            historial.setPrecioUnitario(item.getPrecio());
            historial.setPrecioTotal(item.getPrecio() * item.getCantidad());
            historial.setFecha(LocalDateTime.now());

            historialPedidosRepository.save(historial);
        }

        return pedido;
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