package com.uade.tpo.demo.dto;

import com.uade.tpo.demo.entity.Videojuego.CategoriaJuego;
import lombok.Data;

@Data
public class VideojuegoDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private Double precio;
    private String plataforma;
    private CategoriaJuego categoria;
    private Integer stock;

    // Nuevos campos para almacenar las imágenes en formato Base64
    private String foto;  // Imagen principal en formato Base64
    private String foto2;  // Imagen secundaria en formato Base64
}
