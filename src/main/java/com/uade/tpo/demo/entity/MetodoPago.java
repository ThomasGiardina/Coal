package com.uade.tpo.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Data
@Entity
public class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombrePropietario;

    @Column(nullable = false)
    private String numeroTarjeta;

    @Column(nullable = false)
    private String codigoSeguridad;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date fechaVencimiento;

    @Column(nullable = false)
    private String direccion;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonBackReference
    private Usuario usuario;
}