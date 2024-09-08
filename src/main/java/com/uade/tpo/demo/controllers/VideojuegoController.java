package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.dto.VideojuegoDTO;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.Videojuego;
import com.uade.tpo.demo.entity.Videojuego.CategoriaJuego;
import com.uade.tpo.demo.service.VideojuegoService;
import com.uade.tpo.demo.repository.UserRepository;
import com.uade.tpo.demo.controllers.config.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/videojuegos")
public class VideojuegoController {

    private final VideojuegoService videojuegoService;
    private final UserRepository userRepository;  // Inyectar UserRepository
    private final JwtService jwtService;  // Inyección de JwtService

    @Autowired
    public VideojuegoController(VideojuegoService videojuegoService, UserRepository userRepository, JwtService jwtService) {
        this.videojuegoService = videojuegoService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;  // Inicialización de JwtService
    }

    // Crear un nuevo videojuego
    @PostMapping
    public ResponseEntity<VideojuegoDTO> crearVideojuego(@RequestHeader("Authorization") String token, @RequestBody VideojuegoDTO videojuegoDTO) {
        // Extraer el token JWT del header "Authorization"
        String jwt = token.substring(7); // Remover "Bearer " del token

        // Usar JwtService para obtener el email del usuario desde el token
        String userEmail = jwtService.extractUsername(jwt);

        // Buscar el usuario en la base de datos por su email
        Usuario usuario = userRepository.findByEmail(userEmail)
                            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Verificar que el usuario tenga el rol ADMIN
        if (!usuario.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        // Convertir y guardar el videojuego
        Videojuego videojuego = convertirAEntidad(videojuegoDTO);
        Videojuego videojuegoGuardado = videojuegoService.crearVideojuego(videojuego);
        return ResponseEntity.ok(convertirADTO(videojuegoGuardado));
    }

    // Obtener un videojuego por ID
    @GetMapping("/{id}")
    public ResponseEntity<VideojuegoDTO> obtenerVideojuegoPorId(@PathVariable Long id) {
        Videojuego videojuego = videojuegoService.obtenerVideojuegoPorId(id);
        return ResponseEntity.ok(convertirADTO(videojuego));
    }

    // Obtener todos los videojuegos
    @GetMapping
    public ResponseEntity<List<VideojuegoDTO>> obtenerTodosLosVideojuegos() {
        List<Videojuego> videojuegos = videojuegoService.obtenerTodosLosVideojuegos();
        List<VideojuegoDTO> videojuegosDTO = videojuegos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(videojuegosDTO);
    }

    // Actualizar un videojuego existente
    @PutMapping("/{id}")
    public ResponseEntity<VideojuegoDTO> actualizarVideojuego(@PathVariable Long id, @RequestBody VideojuegoDTO videojuegoDTO) {
        Videojuego datosActualizados = convertirAEntidad(videojuegoDTO);
        Videojuego videojuegoActualizado = videojuegoService.actualizarVideojuego(id, datosActualizados);
        return ResponseEntity.ok(convertirADTO(videojuegoActualizado));
    }

    // Eliminar un videojuego por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVideojuego(@PathVariable Long id) {
        videojuegoService.eliminarVideojuego(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/foto")
    public ResponseEntity<VideojuegoDTO> subirFoto(@PathVariable Long id, @RequestParam("foto") MultipartFile foto) throws IOException {
        Videojuego videojuego = videojuegoService.subirFoto(id, foto);
        return ResponseEntity.ok(convertirADTO(videojuego));
    }

    // Agregar stock a un videojuego
    @PostMapping("/{id}/agregarStock")
    public ResponseEntity<VideojuegoDTO> agregarStock(@PathVariable Long id, @RequestParam int cantidad) {
        Videojuego videojuego = videojuegoService.agregarStock(id, cantidad);
        return ResponseEntity.ok(convertirADTO(videojuego));
    }

    // Disminuir stock de un videojuego
    @PostMapping("/{id}/disminuirStock")
    public ResponseEntity<VideojuegoDTO> disminuirStock(@PathVariable Long id, @RequestParam int cantidad) {
        Videojuego videojuego = videojuegoService.disminuirStock(id, cantidad);
        return ResponseEntity.ok(convertirADTO(videojuego));
    }

    // Buscar videojuegos por título
    @GetMapping("/buscarPorTitulo")
    public ResponseEntity<List<VideojuegoDTO>> buscarPorTitulo(@RequestParam String titulo) {
        List<Videojuego> videojuegos = videojuegoService.buscarPorTitulo(titulo);
        List<VideojuegoDTO> videojuegosDTO = videojuegos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(videojuegosDTO);
    }

    // Buscar videojuegos por plataforma
    @GetMapping("/buscarPorPlataforma")
    public ResponseEntity<List<VideojuegoDTO>> buscarPorPlataforma(@RequestParam String plataforma) {
        List<Videojuego> videojuegos = videojuegoService.buscarPorPlataforma(plataforma);
        List<VideojuegoDTO> videojuegosDTO = videojuegos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(videojuegosDTO);
    }

    @GetMapping("/buscarPorCategoria")
    public ResponseEntity<List<VideojuegoDTO>> buscarPorCategoria(@RequestParam CategoriaJuego categoria) {
        List<Videojuego> videojuegos = videojuegoService.buscarPorCategoria(categoria);
        List<VideojuegoDTO> videojuegosDTO = videojuegos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(videojuegosDTO);
    }

    // Métodos de utilidad para convertir entre DTO y Entidad

    private VideojuegoDTO convertirADTO(Videojuego videojuego) {
        VideojuegoDTO dto = new VideojuegoDTO();
        dto.setId(videojuego.getId());
        dto.setTitulo(videojuego.getTitulo());
        dto.setDescripcion(videojuego.getDescripcion());
        dto.setPrecio(videojuego.getPrecio());
        dto.setPlataforma(videojuego.getPlataforma());
        dto.setCategoria(videojuego.getCategoria());
        dto.setStock(videojuego.getStock());
        dto.setFotoUrl(videojuego.getFotoUrl());
        return dto;
    }

    private Videojuego convertirAEntidad(VideojuegoDTO dto) {
        Videojuego videojuego = new Videojuego();
        videojuego.setId(dto.getId());
        videojuego.setTitulo(dto.getTitulo());
        videojuego.setDescripcion(dto.getDescripcion());
        videojuego.setPrecio(dto.getPrecio());
        videojuego.setPlataforma(dto.getPlataforma());
        videojuego.setCategoria(dto.getCategoria());
        videojuego.setStock(dto.getStock());
        videojuego.setFotoUrl(dto.getFotoUrl());
        return videojuego;
    }
}