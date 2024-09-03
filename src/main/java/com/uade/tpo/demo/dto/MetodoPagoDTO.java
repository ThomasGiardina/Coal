package com.uade.tpo.demo.dto;

import lombok.Data;

import java.util.Date;

@Data
public class MetodoPagoDTO {
    private Long id;
    private Long usuarioId;  // Campo para almacenar el ID del usuario
    private String nombrePropietario;
    private String numeroTarjeta;
    private String codigoSeguridad;
    private Date fechaVencimiento;  // Ajustado para manejar la fecha como Date
    private String direccion;
}