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
        @RequestParam("titulo") String titulo,
        @RequestParam("descripcion") String descripcion,
        @RequestParam("precio") Double precio,
        @RequestParam("plataforma") String plataforma,
        @RequestParam("categoria") CategoriaJuego categoria,
        @RequestParam("stock") Integer stock,
        @RequestParam(value = "foto", required = false) MultipartFile foto,
        @RequestParam(value = "foto2", required = false) MultipartFile foto2
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
            videojuego.setTitulo(titulo);
            videojuego.setDescripcion(descripcion);
            videojuego.setPrecio(precio);
            videojuego.setPlataforma(plataforma);
            videojuego.setCategoria(categoria);
            videojuego.setStock(stock);

            // Guardar el videojuego y las imágenes como bytes
            videojuegoService.crearVideojuego(videojuego, foto, foto2);

            // Retornar el DTO del videojuego creado
            return ResponseEntity.ok(convertirADTO(videojuego));

        } catch (IllegalArgumentException e) {
            // Captura de errores de validación como "Usuario no encontrado"
            return ResponseEntity.status(400).body(null); // Bad Request

        } catch (IOException e) {
            // Captura de errores relacionados con la carga de archivos
            e.printStackTrace(); // Imprime el error en los logs del servidor para depuración
            return ResponseEntity.status(500).body(null); // Internal Server Error

        } catch (Exception e) {
            // Captura de cualquier otra excepción inesperada
            e.printStackTrace(); // Imprime el error en los logs del servidor para depuración
            return ResponseEntity.status(500).body(null); // Internal Server Error
        }
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
    @DeleteMapping
    public ResponseEntity<Void> eliminarVideojuego(@RequestBody IdRequest idRequest) {
        videojuegoService.eliminarVideojuego(idRequest.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/foto")
    public ResponseEntity<VideojuegoDTO> subirFoto(@PathVariable Long id, @RequestParam("foto") MultipartFile foto) throws IOException {
        Videojuego videojuego = videojuegoService.subirFoto(id, foto);
        return ResponseEntity.ok(convertirADTO(videojuego));
    }

    @PostMapping("/{id}/foto2")
    public ResponseEntity<VideojuegoDTO> subirFoto2(@PathVariable Long id, @RequestParam("foto2") MultipartFile foto2) throws IOException {
        Videojuego videojuego = videojuegoService.subirFoto2(id, foto2);
        return ResponseEntity.ok(convertirADTO(videojuego));
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
        dto.setCategoria(videojuego.getCategoria());
        dto.setStock(videojuego.getStock());
        // No incluir fotoUrl ni fotoUrl2 en el DTO ya que son datos binarios
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
        videojuego.setCategoria(dto.getCategoria());
        videojuego.setStock(dto.getStock());
        // No incluir setFotoUrl ni setFotoUrl2 ya que las imágenes no están en el DTO
        return videojuego;
    }

}