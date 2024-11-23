package com.uade.tpo.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

import com.uade.tpo.demo.entity.Carrito;
import com.uade.tpo.demo.entity.ItemPedido;
import com.uade.tpo.demo.entity.MetodoPago;
import com.uade.tpo.demo.entity.Pedido;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.Videojuego;
import com.uade.tpo.demo.entity.Pedido.EstadoPedido;
import com.uade.tpo.demo.repository.PedidoRepository;
import com.uade.tpo.demo.repository.MetodoPagoRepository;
import com.uade.tpo.demo.repository.VideojuegoRepository;


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
    private MetodoPagoService metodoPagoService;

    @Autowired
    private VideojuegoService videojuegoService;

    @Autowired
    private VideojuegoRepository videojuegoRepository;

    @Transactional
    public Pedido crearPedido(
        Long carritoId,
        String tipoEntrega,
        Long metodoPagoId,
        String direccionEnvio,
        String nombreComprador,
        double montoTotal,
        int cantidadArticulos,
        String estadoPedido,
        Long idComprador
    ) {
        Carrito carrito = carritoService.getCarritoById(carritoId);
        if (carrito == null) {
            throw new RuntimeException("Carrito no encontrado");
        }
        MetodoPago metodoPago = metodoPagoId != null ? metodoPagoService.obtenerMetodoPagoPorId(metodoPagoId) : null;

        Pedido pedido = new Pedido();
        pedido.setCantidadArticulos(cantidadArticulos);
        pedido.setDireccionEnvio(direccionEnvio);
        pedido.setEstadoPedido(Pedido.EstadoPedido.valueOf(estadoPedido.toUpperCase())); // Enum
        pedido.setFecha(LocalDateTime.now());
        pedido.setMontoTotal(montoTotal);
        pedido.setNombreComprador(nombreComprador);
        pedido.setTipoEntrega(Pedido.TipoEntrega.valueOf(tipoEntrega.toUpperCase()));
        pedido.setTipoPago(metodoPago != null ? metodoPago.getTipoPago() : MetodoPago.TipoPago.EFECTIVO);
        pedido.setUsuarioComprador(carrito.getUsuario().getUsername());
        pedido.setComprador(carrito.getUsuario());
        pedido.setMetodoPago(metodoPago); 

        List<ItemPedido> itemsPedido = carrito.getItems().stream().map(itemCarrito -> {
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setVideojuego(itemCarrito.getVideojuego());
            itemPedido.setCantidad(itemCarrito.getCantidad());
            itemPedido.setPrecio(itemCarrito.getVideojuego().getPrecio());
            return itemPedido;
        }).collect(Collectors.toList());
        pedido.setProductosAdquiridos(itemsPedido);

        Pedido nuevoPedido = pedidoRepository.save(pedido);

        carritoService.vaciarCarrito(carrito);

        return nuevoPedido;
    }

    @Transactional(rollbackOn = Exception.class)
    public Pedido pagarPedido(Long pedidoId, Long metodoPagoId) {
        try {
            Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

            if (pedido.getEstadoPedido() != EstadoPedido.PENDIENTE) {
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
            pedido.setEstadoPedido(EstadoPedido.CONFIRMADO);
            Pedido pedidoConfirmado = pedidoRepository.save(pedido);

            return pedidoConfirmado;

        } catch (Exception e) {
            throw new RuntimeException("Error al procesar el pago: " + e.getMessage());
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public Pedido pagarPedidoUnico(Long pedidoId, MetodoPago metodoPago) {
        try {
            Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

            if (pedido.getEstadoPedido() != EstadoPedido.PENDIENTE) {
                throw new RuntimeException("El pedido no está en estado pendiente");
            }

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

            pedido.setEstadoPedido(EstadoPedido.CONFIRMADO);
            Pedido pedidoConfirmado = pedidoRepository.save(pedido);

            return pedidoConfirmado;

        } catch (Exception e) {
            throw new RuntimeException("Error al procesar el pago: " + e.getMessage());
        }
    }

    private void validarDatosTarjeta(MetodoPago metodoPago) {
        if (metodoPago.getNumeroTarjeta() == null || metodoPago.getCodigoSeguridad() == null || metodoPago.getFechaVencimiento() == null) {
            throw new RuntimeException("Los datos de la tarjeta son obligatorios para el tipo de pago seleccionado.");
        }
    }

    @Transactional
    public void cancelarPedidosPendientes() {
        List<Pedido> pedidosPendientes = pedidoRepository.findAll().stream()
            .filter(pedido -> pedido.getEstadoPedido() == EstadoPedido.PENDIENTE && pedido.getFecha().isBefore(LocalDateTime.now().minusHours(1)))
            .collect(Collectors.toList());

        for (Pedido pedido : pedidosPendientes) {
            pedido.setEstadoPedido(EstadoPedido.CANCELADO);
            pedidoRepository.save(pedido);
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public Pedido pagarPedidoConValidacionCVV(Long pedidoId, Long metodoPagoId, String cvv) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (pedido.getEstadoPedido() != EstadoPedido.PENDIENTE) {
            throw new RuntimeException("El pedido no está en estado pendiente");
        }

        MetodoPago metodoPago = metodoPagoRepository.findById(metodoPagoId)
            .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

        // Validación del CVV
        if (metodoPago.getTipoPago() == MetodoPago.TipoPago.CREDITO || metodoPago.getTipoPago() == MetodoPago.TipoPago.DEBITO) {
            if (cvv == null || cvv.isEmpty() || !metodoPagoService.validarCVV(metodoPago, cvv)) {
                throw new RuntimeException("El CVV ingresado no es válido.");
            }
        }

        switch (metodoPago.getTipoPago()) {
            case EFECTIVO:
                pedido.setMontoTotal(pedido.getMontoTotal() * 0.85);
                break;
            case DEBITO:
                pedido.setMontoTotal(pedido.getMontoTotal() * 0.90);
                break;
            case CREDITO:
                break;
            default:
                throw new RuntimeException("Tipo de pago no soportado");
        }

        pedido.setMetodoPago(metodoPago);
        pedido.setEstadoPedido(EstadoPedido.CONFIRMADO);
        Pedido pedidoConfirmado = pedidoRepository.save(pedido);

        for (ItemPedido item : pedido.getProductosAdquiridos()) {
            Videojuego videojuego = item.getVideojuego();
            videojuego.setVentas(videojuego.getVentas() + item.getCantidad());
            videojuegoRepository.save(videojuego);
        }

        return pedidoConfirmado;
    }

    @Transactional
    public Pedido crearPedidoDesdeCarrito(
        Long carritoId,
        String tipoEntrega,
        Long metodoPagoId,
        String direccionEnvio,
        List<Map<String, Object>> itemsCarrito
    ) {
        Carrito carrito = carritoService.getCarritoById(carritoId);
        if (carrito == null) {
            throw new RuntimeException("Carrito no encontrado");
        }

        MetodoPago metodoPago = metodoPagoService.obtenerMetodoPagoPorId(metodoPagoId);
        if (metodoPago == null) {
            throw new RuntimeException("Método de pago no encontrado");
        }

        List<ItemPedido> itemsPedido = itemsCarrito.stream().map(item -> {
            ItemPedido itemPedido = new ItemPedido();
            Long videojuegoId = Long.valueOf(item.get("videojuegoId").toString());
            Videojuego videojuego = videojuegoService.obtenerVideojuegoPorId(videojuegoId);

            if (videojuego.getStock() < (Integer) item.get("cantidad")) {
                throw new RuntimeException("Stock insuficiente para el videojuego: " + videojuego.getTitulo());
            }

            itemPedido.setVideojuego(videojuego);
            itemPedido.setCantidad((Integer) item.get("cantidad"));
            itemPedido.setPrecio((Double) item.get("precio"));
            return itemPedido;
        }).collect(Collectors.toList());

        Usuario usuario = carrito.getUsuario();
        Pedido pedido = new Pedido();
        pedido.setComprador(usuario);
        pedido.setNombreComprador(usuario.getFirstName() + " " + usuario.getLastName());
        pedido.setUsuarioComprador(usuario.getUsername());
        pedido.setMetodoPago(metodoPago);
        pedido.setTipoPago(metodoPago.getTipoPago());
        pedido.setTipoEntrega(Pedido.TipoEntrega.valueOf(tipoEntrega.toUpperCase()));
        pedido.setDireccionEnvio(direccionEnvio);
        pedido.setProductosAdquiridos(itemsPedido);
        pedido.setMontoTotal(itemsPedido.stream()
                .mapToDouble(item -> item.getPrecio() * item.getCantidad())
                .sum());
        pedido.setCantidadArticulos(itemsPedido.size());
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstadoPedido(Pedido.EstadoPedido.PENDIENTE);

        Pedido nuevoPedido = pedidoRepository.save(pedido);

        carritoService.vaciarCarrito(carrito);

        return nuevoPedido;
    }

}