package com.uade.tpo.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ItemCarrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "videojuego_id")
    private Videojuego videojuego;

    @ManyToOne
    @JoinColumn(name = "carrito_id")
    private Carrito carrito;

    private Integer cantidad;

    private String titulo;
    private Double precio;
}