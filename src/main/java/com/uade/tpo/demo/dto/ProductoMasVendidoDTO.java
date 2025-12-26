package com.uade.tpo.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductoMasVendidoDTO {
    private Long id;
    private String titulo;
    private int ventas;
    private String fotoUrl;
    private String plataforma;
}