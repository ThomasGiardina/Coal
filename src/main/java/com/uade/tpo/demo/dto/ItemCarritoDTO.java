package com.uade.tpo.demo.dto;

import com.uade.tpo.demo.entity.Videojuego;

import lombok.Data;

@Data
public class ItemCarritoDTO {
    private Long id;
    private Long carritoId;  
    private Long videojuegoId;
    private String titulo;
    private Double precio;
    private Integer cantidad;
    private Videojuego videojuego;
    private String plataforma; 
}