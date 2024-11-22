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
import com.uade.tpo.demo.exception.InsufficientStockException;
import com.uade.tpo.demo.entity.Pedido.EstadoPedido;
import com.uade.tpo.demo.repository.PedidoRepository;
import com.uade.tpo.demo.repository.MetodoPagoRepository;
import com.uade.tpo.demo.repository.VideojuegoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import jakarta.transaction.Transactional;

@Service
public class PedidoService {

    private static final Logger logger = LoggerFactory.getLogger(PedidoService.class);

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
    public Pedido crearPedido(Carrito carrito, Usuario usuario, String tipoEntrega, MetodoPago metodoPago, Map<String, String> direccionEnvio) {
        try {
            logger.info("Iniciando la creación del pedido. Carrito ID: {}, Usuario: {}, TipoEntrega: {}, MetodoPago: {}",
                    carrito != null ? carrito.getId() : "NULO",
                    usuario != null ? usuario.getId() : "NULO",
                    tipoEntrega,
                    metodoPago);

            if (carrito == null || usuario == null || tipoEntrega == null) {
                throw new IllegalArgumentException("Datos insuficientes para crear el pedido.");
            }

            Pedido pedido = new Pedido();
            pedido.setComprador(usuario);
            pedido.setNombreComprador(usuario.getFirstName() + " " + usuario.getLastName());
            pedido.setUsuarioComprador(usuario.getUsername());
            pedido.setMetodoPago(metodoPago);
            pedido.setTipoPago(metodoPago.getTipoPago());
            pedido.setTipoEntrega(Pedido.TipoEntrega.valueOf(tipoEntrega.toUpperCase()));
            pedido.setFecha(LocalDateTime.now());
            pedido.setEstadoPedido(Pedido.EstadoPedido.PENDIENTE);

            logger.info("Datos iniciales del pedido asignados: {}", pedido);

            if ("DELIVERY".equalsIgnoreCase(tipoEntrega)) {
                if (direccionEnvio == null || direccionEnvio.isEmpty()) {
                    throw new IllegalArgumentException("La dirección de envío es requerida para el tipo de entrega DELIVERY.");
                }

                String direccion = direccionEnvio.get("direccion");
                String ciudad = direccionEnvio.get("localidad");
                String codigoPostal = direccionEnvio.get("codigoPostal");
                String telefono = direccionEnvio.get("telefono");

                if (direccion == null || ciudad == null || codigoPostal == null || telefono == null) {
                    throw new IllegalArgumentException("La dirección de envío está incompleta.");
                }

                pedido.setDireccionEnvio(String.format("%s, %s, %s, Tel: %s", direccion, ciudad, codigoPostal, telefono));
            }

            logger.info("Dirección de envío asignada: {}", pedido.getDireccionEnvio());

            List<ItemPedido> items = carrito.getItems().stream().map(itemCarrito -> {
                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setVideojuego(itemCarrito.getVideojuego());
                itemPedido.setCantidad(itemCarrito.getCantidad());
                itemPedido.setPrecio(itemCarrito.getPrecio());
                return itemPedido;
            }).collect(Collectors.toList());

            pedido.setProductosAdquiridos(items);
            pedido.setMontoTotal(items.stream().mapToDouble(item -> item.getPrecio() * item.getCantidad()).sum());
            pedido.setCantidadArticulos(items.size());

            logger.info("Monto total calculado: {}, Cantidad de artículos: {}", pedido.getMontoTotal(), pedido.getCantidadArticulos());

            Pedido nuevoPedido = pedidoRepository.save(pedido);

            logger.info("Pedido guardado en la base de datos. ID: {}", nuevoPedido.getId());

            carritoService.vaciarCarrito(carrito);

            logger.info("Carrito ID: {} vaciado exitosamente.", carrito.getId());

            return nuevoPedido;
        } catch (Exception e) {
            logger.error("Error al crear el pedido: {}", e.getMessage(), e);
            throw e;
        }
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
                // No hay descuento adicional
                break;
            default:
                throw new RuntimeException("Tipo de pago no soportado");
        }

        pedido.setMetodoPago(metodoPago);
        pedido.setEstadoPedido(EstadoPedido.CONFIRMADO);
        Pedido pedidoConfirmado = pedidoRepository.save(pedido);

        // Actualizar estadísticas de ventas de los videojuegos
        for (ItemPedido item : pedido.getProductosAdquiridos()) {
            Videojuego videojuego = item.getVideojuego();
            videojuego.setVentas(videojuego.getVentas() + item.getCantidad());
            videojuegoRepository.save(videojuego);
        }

        return pedidoConfirmado;
    }
}