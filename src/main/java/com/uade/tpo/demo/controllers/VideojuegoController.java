package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.dto.IdRequest;
import com.uade.tpo.demo.dto.VideojuegoDTO;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.Videojuego;
import com.uade.tpo.demo.entity.Videojuego.CategoriaJuego;
import com.uade.tpo.demo.exception.VideojuegoNotFoundException;
import com.uade.tpo.demo.service.VideojuegoService;
import com.uade.tpo.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uade.tpo.demo.controllers.config.JwtService;

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
    private final UserRepository userRepository;  
    private final JwtService jwtService;  

    public VideojuegoController(VideojuegoService videojuegoService, UserRepository userRepository, JwtService jwtService) {
        this.videojuegoService = videojuegoService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;  
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<VideojuegoDTO> crearVideojuego(
        @RequestHeader("Authorization") String token,
        @RequestParam("videojuego") String videojuegoJson,  
        @RequestParam(value = "foto", required = false) MultipartFile foto,  
        @RequestParam(value = "carruselImagen1", required = false) MultipartFile carruselImagen1,
        @RequestParam(value = "carruselImagen2", required = false) MultipartFile carruselImagen2,
        @RequestParam(value = "carruselImagen3", required = false) MultipartFile carruselImagen3
    ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            VideojuegoDTO videojuegoDTO = objectMapper.readValue(videojuegoJson, VideojuegoDTO.class);

            Videojuego videojuego = new Videojuego();
            videojuego.setTitulo(videojuegoDTO.getTitulo());
            videojuego.setDescripcion(videojuegoDTO.getDescripcion());
            videojuego.setPrecio(videojuegoDTO.getPrecio());
            videojuego.setPlataforma(videojuegoDTO.getPlataforma());
            videojuego.setCategorias(videojuegoDTO.getCategorias());
            videojuego.setStock(videojuegoDTO.getStock());
            videojuego.setFechaLanzamiento(videojuegoDTO.getFechaLanzamiento());
            videojuego.setDesarrolladora(videojuegoDTO.getDesarrolladora());

            if (foto != null && !foto.isEmpty()) {
                videojuego.setFoto(foto.getBytes());
            }
            if (carruselImagen1 != null && !carruselImagen1.isEmpty()) {
                videojuego.setCarruselImagen1(carruselImagen1.getBytes());
            }
            if (carruselImagen2 != null && !carruselImagen2.isEmpty()) {
                videojuego.setCarruselImagen2(carruselImagen2.getBytes());
            }
            if (carruselImagen3 != null && !carruselImagen3.isEmpty()) {
                videojuego.setCarruselImagen3(carruselImagen3.getBytes());
            }

            videojuegoService.crearVideojuego(videojuego);
            return ResponseEntity.ok(convertirADTO(videojuego));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    // Endpoint para subir la imagen principal en el caso de que falle el form data
    @PostMapping("/{id}/foto")
    public ResponseEntity<VideojuegoDTO> subirFoto(@PathVariable Long id, @RequestParam("foto") MultipartFile foto) throws IOException {
        Videojuego videojuego = videojuegoService.subirFoto(id, foto);
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
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<VideojuegoDTO> actualizarVideojuego(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestParam("videojuego") String videojuegoJson, 
            @RequestParam(value = "foto", required = false) MultipartFile foto,  
            @RequestParam(value = "carruselImagen1", required = false) MultipartFile carruselImagen1,
            @RequestParam(value = "carruselImagen2", required = false) MultipartFile carruselImagen2,
            @RequestParam(value = "carruselImagen3", required = false) MultipartFile carruselImagen3
    ) {
        try {
            String jwt = token.substring(7);
            String userEmail = jwtService.extractUsername(jwt);
            Usuario usuario = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            if (!usuario.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                return ResponseEntity.status(403).build(); 
            }

            Videojuego videojuegoExistente = videojuegoService.obtenerVideojuegoPorId(id);

            ObjectMapper objectMapper = new ObjectMapper();
            VideojuegoDTO videojuegoDTO = objectMapper.readValue(videojuegoJson, VideojuegoDTO.class);

            videojuegoExistente.setTitulo(videojuegoDTO.getTitulo());
            videojuegoExistente.setDescripcion(videojuegoDTO.getDescripcion());
            videojuegoExistente.setPrecio(videojuegoDTO.getPrecio());
            videojuegoExistente.setPlataforma(videojuegoDTO.getPlataforma());
            videojuegoExistente.setCategorias(videojuegoDTO.getCategorias());
            videojuegoExistente.setStock(videojuegoDTO.getStock());
            videojuegoExistente.setFechaLanzamiento(videojuegoDTO.getFechaLanzamiento());
            videojuegoExistente.setDesarrolladora(videojuegoDTO.getDesarrolladora());

            if (foto != null && !foto.isEmpty()) {
                videojuegoExistente.setFoto(foto.getBytes());
            }
            if (carruselImagen1 != null && !carruselImagen1.isEmpty()) {
                videojuegoExistente.setCarruselImagen1(carruselImagen1.getBytes());
            }
            if (carruselImagen2 != null && !carruselImagen2.isEmpty()) {
                videojuegoExistente.setCarruselImagen2(carruselImagen2.getBytes());
            }
            if (carruselImagen3 != null && !carruselImagen3.isEmpty()) {
                videojuegoExistente.setCarruselImagen3(carruselImagen3.getBytes());
            }

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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVideojuego(@PathVariable Long id) {
        try {
            videojuegoService.eliminarVideojuego(id);
            return ResponseEntity.noContent().build(); 
        } catch (VideojuegoNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
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
        dto.setFoto(videojuego.getFoto() != null ? Base64.getEncoder().encodeToString(videojuego.getFoto()) : null);
    
        // Agregar imágenes del carrusel
        dto.setCarruselImagen1(videojuego.getCarruselImagen1() != null ? Base64.getEncoder().encodeToString(videojuego.getCarruselImagen1()) : null);
        dto.setCarruselImagen2(videojuego.getCarruselImagen2() != null ? Base64.getEncoder().encodeToString(videojuego.getCarruselImagen2()) : null);
        dto.setCarruselImagen3(videojuego.getCarruselImagen3() != null ? Base64.getEncoder().encodeToString(videojuego.getCarruselImagen3()) : null);
    
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
        videojuego.setFoto(dto.getFoto() != null ? Base64.getDecoder().decode(dto.getFoto()) : null);
    
        // Agregar imágenes del carrusel
        videojuego.setCarruselImagen1(dto.getCarruselImagen1() != null ? Base64.getDecoder().decode(dto.getCarruselImagen1()) : null);
        videojuego.setCarruselImagen2(dto.getCarruselImagen2() != null ? Base64.getDecoder().decode(dto.getCarruselImagen2()) : null);
        videojuego.setCarruselImagen3(dto.getCarruselImagen3() != null ? Base64.getDecoder().decode(dto.getCarruselImagen3()) : null);
    
        return videojuego;
    }    

}