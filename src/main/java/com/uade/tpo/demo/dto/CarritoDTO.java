package com.uade.tpo.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarritoDTO {
    private Long id; // ID del carrito.
    private Long usuarioId; // ID del usuario asociado al carrito.
}
