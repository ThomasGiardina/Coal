package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.MetodoPago;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.exception.MetodoPagoNotFoundException;

import java.util.List;

public interface MetodoPagoService {
    MetodoPago crearMetodoPago(MetodoPago metodoPago);
    MetodoPago obtenerMetodoPagoPorId(Long metodoPagoId) throws MetodoPagoNotFoundException;
    List<MetodoPago> obtenerTodosLosMetodosPago();
    MetodoPago actualizarMetodoPago(Long metodoPagoId, MetodoPago datosActualizados) throws MetodoPagoNotFoundException;
    void eliminarMetodoPago(Long metodoPagoId) throws MetodoPagoNotFoundException;
    List<MetodoPago> obtenerMetodosPagoPorUsuario(Usuario usuario);
}