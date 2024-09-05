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
        metodoPago.setNumeroTarjeta(datosActualizados.getNumeroTarjeta());
        metodoPago.setCodigoSeguridad(datosActualizados.getCodigoSeguridad());
        metodoPago.setFechaVencimiento(datosActualizados.getFechaVencimiento());
        metodoPago.setDireccion(datosActualizados.getDireccion());
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