package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.dto.IdRequest;
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
import java.util.Base64;
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

    @PostMapping
    public ResponseEntity<VideojuegoDTO> crearVideojuego(
        @RequestHeader("Authorization") String token,
        @RequestBody VideojuegoDTO videojuegoDTO  // Aquí manejas los datos del videojuego en formato JSON
    ) {
        try {
            // Validar token y usuario con rol ADMIN
            String jwt = token.substring(7);
            String userEmail = jwtService.extractUsername(jwt);
            Usuario usuario = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            if (!usuario.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                return ResponseEntity.status(403).build(); // Forbidden
            }

            // Crear el objeto videojuego
            Videojuego videojuego = new Videojuego();
            videojuego.setTitulo(videojuegoDTO.getTitulo());
            videojuego.setDescripcion(videojuegoDTO.getDescripcion());
            videojuego.setPrecio(videojuegoDTO.getPrecio());
            videojuego.setPlataforma(videojuegoDTO.getPlataforma());
            videojuego.setCategorias(videojuegoDTO.getCategorias());
            videojuego.setStock(videojuegoDTO.getStock());
            videojuego.setFechaLanzamiento(videojuegoDTO.getFechaLanzamiento());
            videojuego.setDesarrolladora(videojuegoDTO.getDesarrolladora());

            // Guardar el videojuego
            videojuegoService.crearVideojuego(videojuego);

            // Retornar el DTO del videojuego creado
            return ResponseEntity.ok(convertirADTO(videojuego));

        } catch (IllegalArgumentException e) {
            // Captura de errores de validación como "Usuario no encontrado"
            return ResponseEntity.status(400).body(null); // Bad Request

        } catch (Exception e) {
            // Captura de cualquier otra excepción inesperada
            e.printStackTrace(); // Imprime el error en los logs del servidor para depuración
            return ResponseEntity.status(500).body(null); // Internal Server Error
        }
    }


    // Endpoint para subir la imagen principal
    @PostMapping("/{id}/foto")
    public ResponseEntity<VideojuegoDTO> subirFoto(@PathVariable Long id, @RequestParam("foto") MultipartFile foto) throws IOException {
        Videojuego videojuego = videojuegoService.subirFoto(id, foto);
        return ResponseEntity.ok(convertirADTO(videojuego));
    }

    // Endpoint para subir la segunda imagen
    @PostMapping("/{id}/foto2")
    public ResponseEntity<VideojuegoDTO> subirFoto2(@PathVariable Long id, @RequestParam("foto2") MultipartFile foto2) throws IOException {
        Videojuego videojuego = videojuegoService.subirFoto2(id, foto2);
        return ResponseEntity.ok(convertirADTO(videojuego));
    }

    // Endpoint para subir las imágenes del carrusel
    @PostMapping("/{id}/carrusel")
    public ResponseEntity<VideojuegoDTO> subirCarrusel(@PathVariable Long id, @RequestParam("carrusel") List<MultipartFile> carrusel) throws IOException {
        Videojuego videojuego = videojuegoService.subirCarrusel(id, carrusel);
        return ResponseEntity.ok(convertirADTO(videojuego));
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
    public ResponseEntity<VideojuegoDTO> actualizarVideojuego(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody VideojuegoDTO videojuegoDTO  // Aquí manejas los datos del videojuego en formato JSON
    ) {
        try {
            String jwt = token.substring(7);
            String userEmail = jwtService.extractUsername(jwt);
            Usuario usuario = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            if (!usuario.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                return ResponseEntity.status(403).build(); 
            }

            // Obtener el videojuego existente por ID
            Videojuego videojuegoExistente = videojuegoService.obtenerVideojuegoPorId(id);

            // Actualizar los campos del videojuego con los nuevos datos
            videojuegoExistente.setTitulo(videojuegoDTO.getTitulo());
            videojuegoExistente.setDescripcion(videojuegoDTO.getDescripcion());
            videojuegoExistente.setPrecio(videojuegoDTO.getPrecio());
            videojuegoExistente.setPlataforma(videojuegoDTO.getPlataforma());
            videojuegoExistente.setCategorias(videojuegoDTO.getCategorias());
            videojuegoExistente.setStock(videojuegoDTO.getStock());
            videojuegoExistente.setFechaLanzamiento(videojuegoDTO.getFechaLanzamiento());
            videojuegoExistente.setDesarrolladora(videojuegoDTO.getDesarrolladora());

            // Guardar el videojuego actualizado en la base de datos
            Videojuego videojuegoActualizado = videojuegoService.actualizarVideojuego(id, videojuegoExistente);

            return ResponseEntity.ok(convertirADTO(videojuegoActualizado));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(null); 

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null); 
        }
    }

    // Eliminar un videojuego por ID
    @DeleteMapping
    public ResponseEntity<Void> eliminarVideojuego(@RequestBody IdRequest idRequest) {
        videojuegoService.eliminarVideojuego(idRequest.getId());
        return ResponseEntity.noContent().build();
    }

    // Agregar stock a un videojuego
    @PutMapping("/{id}/agregarStock")
    public ResponseEntity<VideojuegoDTO> agregarStock(@PathVariable Long id, @RequestParam int cantidad) {
        Videojuego videojuego = videojuegoService.agregarStock(id, cantidad);
        return ResponseEntity.ok(convertirADTO(videojuego));
    }

    // Disminuir stock de un videojuego
    @PutMapping("/{id}/disminuirStock")
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

    // Convertir de entidad a DTO
    private VideojuegoDTO convertirADTO(Videojuego videojuego) {
        VideojuegoDTO dto = new VideojuegoDTO();
        dto.setId(videojuego.getId());
        dto.setTitulo(videojuego.getTitulo());
        dto.setDescripcion(videojuego.getDescripcion());
        dto.setPrecio(videojuego.getPrecio());
        dto.setPlataforma(videojuego.getPlataforma());
        dto.setCategorias(videojuego.getCategorias());
        dto.setStock(videojuego.getStock());
        dto.setFechaLanzamiento(videojuego.getFechaLanzamiento());
        dto.setDesarrolladora(videojuego.getDesarrolladora());
        // Convert byte arrays to Base64 strings for the DTO
        dto.setFoto(videojuego.getFoto() != null ? Base64.getEncoder().encodeToString(videojuego.getFoto()) : null);
        dto.setFoto2(videojuego.getFoto2() != null ? Base64.getEncoder().encodeToString(videojuego.getFoto2()) : null);
        dto.setCarrusel(videojuego.getCarrusel() != null ? videojuego.getCarrusel().stream()
                .map(bytes -> Base64.getEncoder().encodeToString(bytes))
                .collect(Collectors.toList()) : null);
        return dto;
    }

    // Convertir de DTO a entidad
    private Videojuego convertirAEntidad(VideojuegoDTO dto) {
        Videojuego videojuego = new Videojuego();
        videojuego.setId(dto.getId());
        videojuego.setTitulo(dto.getTitulo());
        videojuego.setDescripcion(dto.getDescripcion());
        videojuego.setPrecio(dto.getPrecio());
        videojuego.setPlataforma(dto.getPlataforma());
        videojuego.setCategorias(dto.getCategorias());
        videojuego.setStock(dto.getStock());
        videojuego.setFechaLanzamiento(dto.getFechaLanzamiento());
        videojuego.setDesarrolladora(dto.getDesarrolladora());
        // Convert Base64 strings to byte arrays for the entity
        videojuego.setFoto(dto.getFoto() != null ? Base64.getDecoder().decode(dto.getFoto()) : null);
        videojuego.setFoto2(dto.getFoto() != null ? Base64.getDecoder().decode(dto.getFoto2()) : null);
        videojuego.setCarrusel(dto.getCarrusel() != null ? dto.getCarrusel().stream()
                .map(Base64.getDecoder()::decode)
                .collect(Collectors.toList()) : null);
        return videojuego;
    }

}