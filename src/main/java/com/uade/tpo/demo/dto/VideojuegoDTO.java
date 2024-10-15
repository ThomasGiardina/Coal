package com.uade.tpo.demo.dto;

import com.uade.tpo.demo.entity.Videojuego.CategoriaJuego;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
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
    private String foto2;
    private List<String> carrusel;
    private LocalDate fechaLanzamiento;
    private String desarrolladora;
}
