package com.uade.tpo.demo.controllers.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.uade.tpo.demo.service.PedidoService;

@Component
public class PedidoScheduler {

    @Autowired
    private PedidoService pedidoService;

    @Scheduled(fixedRate = 60000) // Ejecutar cada minuto
    public void cancelarPedidosPendientes() {
        pedidoService.cancelarPedidosPendientes();
    }
}