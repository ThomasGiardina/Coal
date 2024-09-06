package com.uade.tpo.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class HistorialPedidos {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="id_pedido", referencedColumnName = "id")
    private Pedido pedido;


    @OneToOne
    @JoinColumn(name="id_comprador", referencedColumnName = "id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name="videjuego_id", referencedColumnName = "id")
    private Videojuego videojuego;

    private Integer cantidad;

    private double precioUnitario;

    private double precioTotal;

    private LocalDateTime fecha;



}
