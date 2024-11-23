package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.entity.MetodoPago;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.repository.UserRepository;
import com.uade.tpo.demo.service.MetodoPagoService;
import com.uade.tpo.demo.controllers.config.JwtService;
import com.uade.tpo.demo.dto.MetodoPagoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/metodosPago")
public class MetodoPagoController {

    private final MetodoPagoService metodoPagoService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public MetodoPagoController(MetodoPagoService metodoPagoService, UserRepository userRepository, JwtService jwtService) {
        this.metodoPagoService = metodoPagoService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<MetodoPagoDTO> crearMetodoPago(@RequestHeader("Authorization") String token, @RequestBody MetodoPagoDTO metodoPagoDTO) {
        MetodoPago metodoPago = convertirAEntidad(metodoPagoDTO);

        String jwt = token.substring(7);

        String userEmail = jwtService.extractUsername(jwt);

        Usuario usuario = userRepository.findByEmail(userEmail)
                            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        metodoPago.setUsuario(usuario);

        if (metodoPago.getTipoPago() == MetodoPago.TipoPago.EFECTIVO) {
            metodoPago.setNumeroTarjeta(null);
            metodoPago.setCodigoSeguridad(null);
            metodoPago.setFechaVencimiento(null);
        } else {
            validarDatosTarjeta(metodoPago);
        }

        MetodoPago metodoPagoGuardado = metodoPagoService.crearMetodoPago(metodoPago);
        return ResponseEntity.ok(convertirADTO(metodoPagoGuardado));
    }


    private void validarDatosTarjeta(MetodoPago metodoPago) {
        if (metodoPago.getNumeroTarjeta() == null || metodoPago.getCodigoSeguridad() == null || metodoPago.getFechaVencimiento() == null) {
            throw new IllegalArgumentException("Los datos de la tarjeta son obligatorios para el tipo de pago seleccionado.");
        }
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

    @GetMapping("/usuario")
    public ResponseEntity<List<MetodoPagoDTO>> obtenerMetodosPagoUsuario(@RequestHeader("Authorization") String token) {

        String jwt = token.substring(7);

        String userEmail = jwtService.extractUsername(jwt);

        Usuario usuario = userRepository.findByEmail(userEmail)
                            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        List<MetodoPago> metodosPago = metodoPagoService.obtenerMetodosPagoPorUsuario(usuario);

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

    private MetodoPagoDTO convertirADTO(MetodoPago metodoPago) {
        MetodoPagoDTO dto = new MetodoPagoDTO();
        dto.setId(metodoPago.getId());
        dto.setUsuarioId(metodoPago.getUsuario().getId());
        dto.setNombrePropietario(metodoPago.getNombrePropietario());
        dto.setNumeroTarjeta(metodoPago.getNumeroTarjeta());
        dto.setCodigoSeguridad(metodoPago.getCodigoSeguridad());
        dto.setFechaVencimiento(metodoPago.getFechaVencimiento());
        dto.setDireccion(metodoPago.getDireccion());
        dto.setTipoPago(metodoPago.getTipoPago());
        return dto;
    }

    private MetodoPago convertirAEntidad(MetodoPagoDTO metodoPagoDTO) {
        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setId(metodoPagoDTO.getId());
        metodoPago.setNombrePropietario(metodoPagoDTO.getNombrePropietario());
        metodoPago.setNumeroTarjeta(metodoPagoDTO.getNumeroTarjeta());
        metodoPago.setCodigoSeguridad(metodoPagoDTO.getCodigoSeguridad());
        metodoPago.setFechaVencimiento(metodoPagoDTO.getFechaVencimiento());
        metodoPago.setDireccion(metodoPagoDTO.getDireccion());
        metodoPago.setTipoPago(metodoPagoDTO.getTipoPago());
        return metodoPago;
    }
}