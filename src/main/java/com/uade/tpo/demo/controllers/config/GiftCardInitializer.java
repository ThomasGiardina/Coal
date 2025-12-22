package com.uade.tpo.demo.controllers.config;

import com.uade.tpo.demo.entity.Videojuego;
import com.uade.tpo.demo.repository.VideojuegoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GiftCardInitializer implements CommandLineRunner {

    @Autowired
    private VideojuegoRepository videojuegoRepository;

    @Override
    public void run(String... args) {
        crearOGarantizarGiftCard("Tarjeta Coal $20", 20.0);
        crearOGarantizarGiftCard("Tarjeta Coal $50", 50.0);
        crearOGarantizarGiftCard("Tarjeta Coal $100", 100.0);
    }

    private void crearOGarantizarGiftCard(String titulo, double precio) {
        Optional<Videojuego> existenteOpt = videojuegoRepository.findByTitulo(titulo);
        if (existenteOpt.isPresent()) {
            Videojuego existente = existenteOpt.get();
            if (!existente.isGiftCard()) {
                existente.setGiftCard(true);
                if (existente.getStock() == null) existente.setStock(Integer.MAX_VALUE);
                videojuegoRepository.save(existente);
            }
            return;
        }

        Videojuego gc = new Videojuego();
        gc.setTitulo(titulo);
        gc.setDescripcion("Tarjeta de regalo para saldo Coal");
        gc.setPrecio(precio);
        gc.setPlataforma("COAL");
        gc.setFechaLanzamiento("2025-01-01");
        gc.setDesarrolladora("Coal");
        gc.setStock(Integer.MAX_VALUE);
        gc.setGiftCard(true);
        // ventas por defecto 0
        videojuegoRepository.save(gc);
    }
}
