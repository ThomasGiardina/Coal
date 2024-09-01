package com.uade.tpo.demo.entity;

import com.uade.tpo.demo.entity.Pedido.EstadoPedido;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Videojuego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private String plataforma;

    @Enumerated(EnumType.STRING)
    private CategoriaJuego categoria;

    @Column(nullable = false)
    private Integer stock;

    public enum CategoriaJuego{
        ACCION,
        AVENTURA,
        RPG,
        SIMULACION,
        DEPORTES,
        ESTRATEGIA,
        PUZZLE,
        TERROR,
        VR,
        EDUCATIVO,
    }
}
