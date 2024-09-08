package com.uade.tpo.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Data
@Entity
public class MetodoPago {

    public enum TipoPago {
        EFECTIVO,
        DEBITO,
        CREDITO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombrePropietario;

    private String numeroTarjeta;

    private String codigoSeguridad;

    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;

    @Column(nullable = false)
    private String direccion;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonBackReference
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPago tipoPago; 
}