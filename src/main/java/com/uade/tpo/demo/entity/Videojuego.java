package com.uade.tpo.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;
import java.util.Set;

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
    @ElementCollection(targetClass = CategoriaJuego.class)
    private Set<CategoriaJuego> categorias;

    @Column(nullable = false)
    private String fechaLanzamiento;

    @Column(nullable = false)
    private String desarrolladora;

    @Column(nullable = false)
    private Integer stock;

    @Lob
    @Column(nullable = true, columnDefinition = "LONGBLOB")
    private byte[] foto;  

    @Lob
    @Column(nullable = true, columnDefinition = "LONGBLOB")
    private byte[] carruselImagen1;

    @Lob
    @Column(nullable = true, columnDefinition = "LONGBLOB")
    private byte[] carruselImagen2;

    @Lob
    @Column(nullable = true, columnDefinition = "LONGBLOB")
    private byte[] carruselImagen3;

    @Column(nullable = false)
    private int ventas;

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

    public enum Plataforma {
        XBOX,
        PC,
        NINTENDO_SWITCH,
        PLAY_STATION
    }
    
}
