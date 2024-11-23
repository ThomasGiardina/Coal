package com.uade.tpo.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.ToString;

@Data
@Entity
public class ItemCarrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "videojuego_id")
    private Videojuego videojuego;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id")
    @JsonBackReference
    private Carrito carrito;

    private Integer cantidad;
    private String titulo;
    private Double precio;
    private String plataforma;
}
