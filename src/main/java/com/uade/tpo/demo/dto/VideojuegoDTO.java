package com.uade.tpo.demo.dto;

import com.uade.tpo.demo.entity.Videojuego.CategoriaJuego;
import lombok.Data;
import java.util.Set;

@Data
public class VideojuegoDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private Double precio;
    private String plataforma;
    private Set<CategoriaJuego> categorias;
    private Integer stock;
    private String foto;
    private String carruselImagen1; 
    private String carruselImagen2; 
    private String carruselImagen3; 
    private String fechaLanzamiento;
    private String desarrolladora;
}
