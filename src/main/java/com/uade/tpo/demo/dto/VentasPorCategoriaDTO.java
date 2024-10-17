package com.uade.tpo.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VentasPorCategoriaDTO {
    private String categoria;
    private int ventas;
}