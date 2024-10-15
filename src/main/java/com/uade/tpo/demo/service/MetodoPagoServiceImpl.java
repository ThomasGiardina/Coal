package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.MetodoPago;
import com.uade.tpo.demo.entity.Pedido;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.exception.MetodoPagoNotFoundException;
import com.uade.tpo.demo.repository.MetodoPagoRepository;
import com.uade.tpo.demo.repository.PedidoRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetodoPagoServiceImpl implements MetodoPagoService {

    private final MetodoPagoRepository metodoPagoRepository;
    private final PedidoRepository pedidoRepository;
    private final PasswordEncoder passwordEncoder;  
    

    public MetodoPagoServiceImpl(MetodoPagoRepository metodoPagoRepository, PedidoRepository pedidoRepository, PasswordEncoder passwordEncoder) {
        this.metodoPagoRepository = metodoPagoRepository;
        this.pedidoRepository = pedidoRepository;
        this.passwordEncoder = passwordEncoder;  
    }

    @Override
    public MetodoPago crearMetodoPago(MetodoPago metodoPago) {
        if (metodoPago.getTipoPago() == MetodoPago.TipoPago.CREDITO || metodoPago.getTipoPago() == MetodoPago.TipoPago.DEBITO) {
            validarDatosTarjeta(metodoPago);
            metodoPago.setCodigoSeguridad(passwordEncoder.encode(metodoPago.getCodigoSeguridad()));
        }
        return metodoPagoRepository.save(metodoPago);
    }

    @Override
    public MetodoPago obtenerMetodoPagoPorId(Long metodoPagoId) throws MetodoPagoNotFoundException {
        return metodoPagoRepository.findById(metodoPagoId)
                .orElseThrow(() -> new MetodoPagoNotFoundException("Método de pago no encontrado"));
    }

    @Override
    public List<MetodoPago> obtenerTodosLosMetodosPago() {
        return metodoPagoRepository.findAll();
    }

    @Override
    public MetodoPago actualizarMetodoPago(Long metodoPagoId, MetodoPago datosActualizados) throws MetodoPagoNotFoundException {
        MetodoPago metodoPago = obtenerMetodoPagoPorId(metodoPagoId);
        metodoPago.setNombrePropietario(datosActualizados.getNombrePropietario());
        metodoPago.setDireccion(datosActualizados.getDireccion());
        metodoPago.setTipoPago(datosActualizados.getTipoPago());

        if (metodoPago.getTipoPago() == MetodoPago.TipoPago.CREDITO || metodoPago.getTipoPago() == MetodoPago.TipoPago.DEBITO) {
            validarDatosTarjeta(datosActualizados);
            metodoPago.setNumeroTarjeta(datosActualizados.getNumeroTarjeta());
            metodoPago.setCodigoSeguridad(null);
            metodoPago.setFechaVencimiento(datosActualizados.getFechaVencimiento());
        } else {
            metodoPago.setNumeroTarjeta(null);
            metodoPago.setCodigoSeguridad(null);
            metodoPago.setFechaVencimiento(null);
        }

        return metodoPagoRepository.save(metodoPago);
    }

    @Override
    public void eliminarMetodoPago(Long metodoPagoId) throws MetodoPagoNotFoundException {
        MetodoPago metodoPago = obtenerMetodoPagoPorId(metodoPagoId);
        metodoPagoRepository.delete(metodoPago);
    }

    @Override
    public List<MetodoPago> obtenerMetodosPagoPorUsuario(Usuario usuario) {
        return metodoPagoRepository.findAllByUsuario(usuario);
    }

    private void validarDatosTarjeta(MetodoPago metodoPago) {
        if (metodoPago.getNumeroTarjeta() == null || metodoPago.getCodigoSeguridad() == null || metodoPago.getFechaVencimiento() == null) {
            throw new IllegalArgumentException("Los datos de la tarjeta son obligatorios para pagos con CREDITO o DEBITO.");
        }
    }
}