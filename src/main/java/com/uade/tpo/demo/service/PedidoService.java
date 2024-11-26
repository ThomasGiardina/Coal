package com.uade.tpo.demo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.tpo.demo.dto.PedidoDTO;
import com.uade.tpo.demo.entity.Carrito;
import com.uade.tpo.demo.entity.ItemPedido;
import com.uade.tpo.demo.entity.MetodoPago;
import com.uade.tpo.demo.entity.Pedido;
import com.uade.tpo.demo.entity.Videojuego;
import com.uade.tpo.demo.entity.Pedido.EstadoPedido;
import com.uade.tpo.demo.repository.PedidoRepository;
import com.uade.tpo.demo.repository.MetodoPagoRepository;
import com.uade.tpo.demo.repository.VideojuegoRepository;
import com.uade.tpo.demo.dto.ItemPedidoDTO;



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
    private VideojuegoRepository videojuegoRepository;

    private double calcularDescuento(double montoTotal, MetodoPago.TipoPago tipoPago) {
        switch (tipoPago) {
            case EFECTIVO:
                return montoTotal * 0.15;
            case DEBITO:
                return montoTotal * 0.10;
            case CREDITO:
                return 0.0;
            default:
                throw new IllegalArgumentException("Tipo de pago no soportado.");
        }
    }

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
            Long idComprador,
            String tipoPago 
    ) {
        Carrito carrito = carritoService.getCarritoById(carritoId);
        if (carrito == null) {
            throw new RuntimeException("Carrito no encontrado");
        }

        MetodoPago metodoPago = null;
        if (metodoPagoId != null) {
            metodoPago = metodoPagoService.obtenerMetodoPagoPorId(metodoPagoId);
        } else if (!"EFECTIVO".equalsIgnoreCase(tipoPago)) {
            throw new IllegalArgumentException("El método de pago es obligatorio para esta forma de entrega.");
        }

        double descuento = (metodoPago != null) ? calcularDescuento(montoTotal, metodoPago.getTipoPago()) : 0.0;
        double montoFinal = montoTotal - descuento;

        Pedido pedido = new Pedido();
        pedido.setCantidadArticulos(cantidadArticulos);
        pedido.setDireccionEnvio(direccionEnvio);
        pedido.setEstadoPedido(Pedido.EstadoPedido.valueOf(estadoPedido.toUpperCase())); 
        pedido.setFecha(LocalDateTime.now());
        pedido.setMontoTotal(montoFinal);
        pedido.setNombreComprador(nombreComprador);
        pedido.setTipoEntrega(Pedido.TipoEntrega.valueOf(tipoEntrega.toUpperCase()));

        if (metodoPago != null) {
            pedido.setMetodoPago(metodoPago);
            pedido.setTipoPago(Pedido.TipoPago.valueOf(metodoPago.getTipoPago().name()));
        } else {
            pedido.setTipoPago(Pedido.TipoPago.EFECTIVO);
        }

        pedido.setUsuarioComprador(carrito.getUsuario().getUsername());
        pedido.setComprador(carrito.getUsuario());

        List<ItemPedido> itemsPedido = carrito.getItems().stream().map(itemCarrito -> {
            Videojuego videojuego = itemCarrito.getVideojuego();

            if (videojuego.getStock() < itemCarrito.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para el videojuego: " + videojuego.getTitulo());
            }

            videojuego.setStock(videojuego.getStock() - itemCarrito.getCantidad());
            videojuegoRepository.save(videojuego);

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setVideojuego(videojuego);
            itemPedido.setCantidad(itemCarrito.getCantidad());
            itemPedido.setPrecio(videojuego.getPrecio());

            itemPedido.setPedido(pedido);

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

    public List<PedidoDTO> getAllPedidos() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        return pedidos.stream()
                .map(pedido -> PedidoDTO.builder()
                        .id(pedido.getId())
                        .fecha(pedido.getFecha().toLocalDate())
                        .cliente(pedido.getNombreComprador())
                        .tipoPago(pedido.getTipoPago().toString())
                        .montoTotal(BigDecimal.valueOf(pedido.getMontoTotal()))
                        .cantidadArticulos(pedido.getCantidadArticulos())
                        .tipoEntrega(pedido.getTipoEntrega().toString())
                        .estadoPedido(pedido.getEstadoPedido().toString())
                        .productosAdquiridos(
                                pedido.getProductosAdquiridos().stream()
                                        .map(item -> ItemPedidoDTO.builder()
                                                .titulo(item.getVideojuego().getTitulo()) 
                                                .cantidad(item.getCantidad())
                                                .build())
                                        .collect(Collectors.toList())
                        )
                        .build())
                .collect(Collectors.toList());
    }

    public List<Pedido> getPedidosByUsuarioId(Long usuarioId) {
        return pedidoRepository.findByCompradorId(usuarioId);
    }

    @Transactional
    public Pedido confirmarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstadoPedido(EstadoPedido.CONFIRMADO);

        for (ItemPedido item : pedido.getProductosAdquiridos()) {
            Videojuego videojuego = item.getVideojuego();
            videojuego.setVentas(videojuego.getVentas() + item.getCantidad());
            videojuegoRepository.save(videojuego); 
        }
        
        return pedidoRepository.save(pedido);
    }


    @Transactional
    public Pedido cambiarAPendiente(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (pedido.getEstadoPedido() != EstadoPedido.CONFIRMADO) {
            throw new RuntimeException("El pedido no está en estado confirmado y no puede cambiar a pendiente.");
        }

        pedido.setEstadoPedido(EstadoPedido.PENDIENTE);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido cancelarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstadoPedido(Pedido.EstadoPedido.CANCELADO);
        return pedidoRepository.save(pedido);
    }

}