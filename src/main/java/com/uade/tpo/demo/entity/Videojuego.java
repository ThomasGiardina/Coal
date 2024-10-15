package com.uade.tpo.demo.entity;

import com.uade.tpo.demo.entity.Pedido.EstadoPedido;

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
import java.time.LocalDate;
import java.util.List;
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
    private LocalDate fechaLanzamiento;

    @Column(nullable = false)
    private String desarrolladora;

    @Column(nullable = false)
    private Integer stock;

    @Lob  // Esto indica que el campo almacenará datos grandes (Large Object)
    @Column(nullable = true)
    private byte[] foto;  // Almacenar la imagen principal como binario

    @Lob
    @Column(nullable = true)
    private byte[] foto2;  // Almacenar la imagen secundaria como binario

    @Lob
    @ElementCollection
    private List<byte[]> carrusel;

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
