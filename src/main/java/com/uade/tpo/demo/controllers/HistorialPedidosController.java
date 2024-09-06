package com.uade.tpo.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


import com.uade.tpo.demo.entity.HistorialPedidos;
import com.uade.tpo.demo.service.HistorialPedidosService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
@RequestMapping("/Historial")
public class HistorialPedidosController {


    @Autowired
    private HistorialPedidosService historialPedidosService;

    @GetMapping("/{id}")
    public HistorialPedidos obtenerHistorialPorId(@PathVariable Long id) {
        return historialPedidosService.getHistorialPorId(id);
    }
    
    @PostMapping
    public HistorialPedidos saveHistorial(@RequestBody HistorialPedidos historialPedidos) {
        return historialPedidosService.saveHistorial(historialPedidos);
    }
}
