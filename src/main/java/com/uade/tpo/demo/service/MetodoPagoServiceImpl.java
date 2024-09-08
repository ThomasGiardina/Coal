package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.MetodoPago;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.exception.MetodoPagoNotFoundException;
import com.uade.tpo.demo.repository.MetodoPagoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetodoPagoServiceImpl implements MetodoPagoService {

    private final MetodoPagoRepository metodoPagoRepository;

    public MetodoPagoServiceImpl(MetodoPagoRepository metodoPagoRepository) {
        this.metodoPagoRepository = metodoPagoRepository;
    }

    @Override
    public MetodoPago crearMetodoPago(MetodoPago metodoPago) {
        if (metodoPago.getTipoPago() == MetodoPago.TipoPago.CREDITO || metodoPago.getTipoPago() == MetodoPago.TipoPago.DEBITO) {
            if (metodoPago.getNumeroTarjeta() == null || metodoPago.getCodigoSeguridad() == null || metodoPago.getFechaVencimiento() == null) {
                throw new IllegalArgumentException("Los datos de la tarjeta son obligatorios para pagos con CREDITO o DEBITO.");
            }
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
            if (datosActualizados.getNumeroTarjeta() == null || datosActualizados.getCodigoSeguridad() == null || datosActualizados.getFechaVencimiento() == null) {
                throw new IllegalArgumentException("Los datos de la tarjeta son obligatorios para pagos con CREDITO o DEBITO.");
            }
            metodoPago.setNumeroTarjeta(datosActualizados.getNumeroTarjeta());
            metodoPago.setCodigoSeguridad(datosActualizados.getCodigoSeguridad());
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
}