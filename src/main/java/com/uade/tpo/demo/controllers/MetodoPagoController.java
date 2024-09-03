package com.uade.tpo.demo.controllers;
import com.uade.tpo.demo.entity.MetodoPago;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.repository.UserRepository;
import com.uade.tpo.demo.service.MetodoPagoService;
import com.uade.tpo.demo.controllers.config.JwtService;
import com.uade.tpo.demo.dto.MetodoPagoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/metodosPago")
public class MetodoPagoController {

    private final MetodoPagoService metodoPagoService;
    private final UserRepository userRepository;  // Inyectar UserRepository
    private final JwtService jwtService;  // Inyección de JwtService

    @Autowired
    public MetodoPagoController(MetodoPagoService metodoPagoService, UserRepository userRepository, JwtService jwtService) {
        this.metodoPagoService = metodoPagoService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;  // Inicialización de JwtService
    }

    @PostMapping
    public ResponseEntity<MetodoPagoDTO> crearMetodoPago(@RequestHeader("Authorization") String token, @RequestBody MetodoPagoDTO metodoPagoDTO) {
        MetodoPago metodoPago = convertirAEntidad(metodoPagoDTO);

        // Extraer el token JWT del header "Authorization"
        String jwt = token.substring(7); // Remover "Bearer " del token

        // Usar JwtService para obtener el email del usuario desde el token
        String userEmail = jwtService.extractUsername(jwt);

        // Buscar el usuario en la base de datos por su email
        Usuario usuario = userRepository.findByEmail(userEmail)
                            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Asignar el usuario al método de pago
        metodoPago.setUsuario(usuario);

        // Guardar el método de pago
        MetodoPago metodoPagoGuardado = metodoPagoService.crearMetodoPago(metodoPago);
        return ResponseEntity.ok(convertirADTO(metodoPagoGuardado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetodoPagoDTO> obtenerMetodoPagoPorId(@PathVariable Long id) {
        MetodoPago metodoPago = metodoPagoService.obtenerMetodoPagoPorId(id);
        return ResponseEntity.ok(convertirADTO(metodoPago));
    }

    @GetMapping
    public ResponseEntity<List<MetodoPagoDTO>> obtenerTodosLosMetodosPago() {
        List<MetodoPago> metodosPago = metodoPagoService.obtenerTodosLosMetodosPago();
        List<MetodoPagoDTO> metodosPagoDTO = metodosPago.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(metodosPagoDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetodoPagoDTO> actualizarMetodoPago(@PathVariable Long id, @RequestBody MetodoPagoDTO metodoPagoDTO) {
        MetodoPago datosActualizados = convertirAEntidad(metodoPagoDTO);
        MetodoPago metodoPagoActualizado = metodoPagoService.actualizarMetodoPago(id, datosActualizados);
        return ResponseEntity.ok(convertirADTO(metodoPagoActualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMetodoPago(@PathVariable Long id) {
        metodoPagoService.eliminarMetodoPago(id);
        return ResponseEntity.noContent().build();
    }

    // Métodos de utilidad para convertir entre DTO y Entidad

    private MetodoPagoDTO convertirADTO(MetodoPago metodoPago) {
        MetodoPagoDTO dto = new MetodoPagoDTO();
        dto.setId(metodoPago.getId());  // Método getId() en lugar de getIdMetodoPago()
        dto.setUsuarioId(metodoPago.getUsuario().getId());  // Método getUsuario() que devuelve un objeto Usuario
        dto.setNombrePropietario(metodoPago.getNombrePropietario());
        dto.setNumeroTarjeta(metodoPago.getNumeroTarjeta());
        dto.setCodigoSeguridad(metodoPago.getCodigoSeguridad());
        dto.setFechaVencimiento(metodoPago.getFechaVencimiento());
        dto.setDireccion(metodoPago.getDireccion());
        return dto;
    }

    private MetodoPago convertirAEntidad(MetodoPagoDTO metodoPagoDTO) {
        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setId(metodoPagoDTO.getId()); 
        // La asignación de usuario debería hacerse fuera de este método
        metodoPago.setNombrePropietario(metodoPagoDTO.getNombrePropietario());
        metodoPago.setNumeroTarjeta(metodoPagoDTO.getNumeroTarjeta());
        metodoPago.setCodigoSeguridad(metodoPagoDTO.getCodigoSeguridad());
        metodoPago.setFechaVencimiento(metodoPagoDTO.getFechaVencimiento());
        metodoPago.setDireccion(metodoPagoDTO.getDireccion());
        return metodoPago;
    }
}