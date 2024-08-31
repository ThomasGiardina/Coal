package com.uade.tpo.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.tpo.demo.entity.MetodoPago;
import com.uade.tpo.demo.repository.MetodoPagoRepository;
import com.uade.tpo.demo.exception.MetodoPagoNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class MetodoPagoService {

    @Autowired
    private MetodoPagoRepository metodoPagoRepository;

    public MetodoPago saveMetodoPago(MetodoPago metodoPago) {
        return metodoPagoRepository.save(metodoPago);
    }

    public List<MetodoPago> getAllMetodosPago() {
        return metodoPagoRepository.findAll();
    }

    public MetodoPago getMetodoPagoById(Long id) throws MetodoPagoNotFoundException {
        return metodoPagoRepository.findById(id)
            .orElseThrow(() -> new MetodoPagoNotFoundException("Método de pago no encontrado"));
    }

    public MetodoPago updateMetodoPago(Long id, MetodoPago metodoPagoDetails) throws MetodoPagoNotFoundException {
        MetodoPago metodoPago = getMetodoPagoById(id);

        metodoPago.setNombrePropietario(metodoPagoDetails.getNombrePropietario());
        metodoPago.setNumeroTarjeta(metodoPagoDetails.getNumeroTarjeta());
        metodoPago.setCodigoSeguridad(metodoPagoDetails.getCodigoSeguridad());
        metodoPago.setFechaVencimiento(metodoPagoDetails.getFechaVencimiento());
        metodoPago.setDireccion(metodoPagoDetails.getDireccion());

        return metodoPagoRepository.save(metodoPago);
    }

    public void deleteMetodoPago(Long id) throws MetodoPagoNotFoundException {
        MetodoPago metodoPago = getMetodoPagoById(id);
        metodoPagoRepository.delete(metodoPago);
    }
}