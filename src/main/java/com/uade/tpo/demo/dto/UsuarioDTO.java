package com.uade.tpo.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;         
    private String username;  
    private String email;     
    private String firstName; 
    private String lastName;  
    private String telefono;  
}