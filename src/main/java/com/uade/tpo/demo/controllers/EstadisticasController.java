package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.dto.ProductoMasVendidoDTO;
import com.uade.tpo.demo.dto.UltimasVentasDTO;
import com.uade.tpo.demo.service.EstadisticasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticasController {

    @Autowired
    private EstadisticasService estadisticasService;

    @GetMapping("/recaudacion-mensual")
    public ResponseEntity<Double> obtenerRecaudacionMensual() {
        return ResponseEntity.ok(estadisticasService.obtenerRecaudacionMensual());
    }

    @GetMapping("/recaudacion-diaria")
    public ResponseEntity<Double> obtenerRecaudacionDiaria() {
        return ResponseEntity.ok(estadisticasService.obtenerRecaudacionDiaria());
    }

    @GetMapping("/ultimas-ventas")
    public ResponseEntity<List<UltimasVentasDTO>> obtenerUltimasVentas() {
        return ResponseEntity.ok(estadisticasService.obtenerUltimasVentas());
    }

    @GetMapping("/productos-mas-vendidos")
    public ResponseEntity<List<ProductoMasVendidoDTO>> obtenerProductosMasVendidos() {
        return ResponseEntity.ok(estadisticasService.obtenerProductosMasVendidos());
    }

    @GetMapping("/ventas-por-categoria")
    public ResponseEntity<Map<String, Integer>> obtenerVentasPorCategoria() {
        return ResponseEntity.ok(estadisticasService.obtenerVentasPorCategoria());
    }

}