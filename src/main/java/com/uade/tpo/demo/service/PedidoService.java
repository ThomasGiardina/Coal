package com.uade.tpo.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.tpo.demo.entity.Carrito;
import com.uade.tpo.demo.entity.EventosHistorial;
import com.uade.tpo.demo.entity.HistorialPedidos;
import com.uade.tpo.demo.entity.ItemPedido;
import com.uade.tpo.demo.entity.MetodoPago;
import com.uade.tpo.demo.entity.Pedido;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.Videojuego;
import com.uade.tpo.demo.exception.InsufficientStockException;
import com.uade.tpo.demo.entity.Pedido.EstadoPedido;
import com.uade.tpo.demo.repository.PedidoRepository;
import com.uade.tpo.demo.repository.EventosHistorialRepository;
import com.uade.tpo.demo.repository.HistorialPedidosRepository;
import com.uade.tpo.demo.repository.MetodoPagoRepository;

import jakarta.transaction.Transactional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private MetodoPagoRepository metodoPagoRepository;

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private VideojuegoService videojuegoService;

    @Autowired
    private HistorialPedidosRepository historialPedidosRepository;

    @Autowired
    private HistorialPedidosService historialPedidosService;

    @Autowired
    private EventosHistorialRepository eventosHistorialRepository;

    @Transactional
    public Pedido crearPedido(Carrito carrito, Usuario usuario) {
        Pedido pedido = new Pedido();
        pedido.setComprador(usuario);
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado(Pedido.EstadoPedido.PENDIENTE);


        carrito.getItems().forEach(itemCarrito -> {
            Videojuego videojuego = itemCarrito.getVideojuego();
            if (videojuego.getStock() < itemCarrito.getCantidad()) {
                throw new InsufficientStockException("No hay suficiente stock para el videojuego: " + videojuego.getTitulo());
            }
        });


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

    @Transactional(rollbackOn = Exception.class)
public Pedido pagarPedido(Long pedidoId, Long metodoPagoId) {
    try {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new RuntimeException("El pedido no está en estado pendiente");
        }

        if (metodoPagoId == null) {
            throw new RuntimeException("Debe seleccionar un método de pago antes de proceder con el pago.");
        }

        MetodoPago metodoPago = metodoPagoRepository.findById(metodoPagoId)
            .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

        switch (metodoPago.getTipoPago()) {
            case EFECTIVO:
                pedido.setMontoTotal(pedido.getMontoTotal() * 0.85);
                break;
            case DEBITO:
                pedido.setMontoTotal(pedido.getMontoTotal() * 0.90);
                validarDatosTarjeta(metodoPago);
                break;
            case CREDITO:
                validarDatosTarjeta(metodoPago);
                break;
            default:
                throw new RuntimeException("Tipo de pago no soportado");
        }

        pedido.setMetodoPago(metodoPago);
        pedido.setEstado(EstadoPedido.CONFIRMADO);
        Pedido pedidoConfirmado = pedidoRepository.save(pedido);

        Long usuarioId = pedido.getComprador().getId();  
        HistorialPedidos historial = historialPedidosService.obtenerHistorialPorUsuario(usuarioId);

        List<ItemPedido> itemsClonados = new ArrayList<>(pedido.getProductosAdquiridos());

        EventosHistorial evento = new EventosHistorial();
        evento.setHistorial(historial);  
        evento.setPedido(pedidoConfirmado);
        evento.setFechaEvento(LocalDateTime.now());
        evento.setPrecioTotal(pedidoConfirmado.getMontoTotal());
        evento.setItems(itemsClonados); 

        eventosHistorialRepository.save(evento);

        return pedidoConfirmado;

    } catch (Exception e) {
        throw new RuntimeException("Error al procesar el pago: " + e.getMessage());
    }
}

    private void registrarEventoHistorial(HistorialPedidos historial, Pedido pedido) {
        EventosHistorial evento = new EventosHistorial();
        evento.setHistorial(historial); 
        evento.setPedido(pedido); 
        evento.setItems(pedido.getProductosAdquiridos());  
        evento.setPrecioTotal(pedido.getMontoTotal());  
        evento.setFechaEvento(LocalDateTime.now());  

        eventosHistorialRepository.save(evento);
    }

    private void validarDatosTarjeta(MetodoPago metodoPago) {
        if (metodoPago.getNumeroTarjeta() == null || metodoPago.getCodigoSeguridad() == null || metodoPago.getFechaVencimiento() == null) {
            throw new RuntimeException("Los datos de la tarjeta son obligatorios para el tipo de pago seleccionado.");
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