package com.uade.tpo.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;         // El ID del usuario
    private String username;  // El nombre de usuario
    private String email;     // El email del usuario
    private String firstName; // El nombre del usuario
    private String lastName;  // El apellido del usuario
    private String telefono;  // El teléfono del usuario
}