package com.uade.tpo.demo.dto;

import lombok.Data;

import java.util.Date;

@Data
public class MetodoPagoDTO {
    private Long id;
    private Long usuarioId; 
    private String nombrePropietario;
    private String numeroTarjeta;
    private String codigoSeguridad;
    private Date fechaVencimiento;
    private String direccion;
}