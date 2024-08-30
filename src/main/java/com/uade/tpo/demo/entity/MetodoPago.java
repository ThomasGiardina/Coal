package com.uade.tpo.demo.entity;

import java.util.Date;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idMetodoPago;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario user;  

    @Column(nullable = false)
    private String nombrePropietario;

    @Column(nullable = false, length = 16)
    private String numeroTarjeta;

    @Column(nullable = false, length = 3)
    private String codigoSeguridad;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date fechaVencimiento;

    @Column(nullable = false)
    private String direccion;
}