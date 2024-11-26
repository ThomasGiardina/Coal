package com.uade.tpo.demo.service;

import com.uade.tpo.demo.dto.ProductoMasVendidoDTO;
import com.uade.tpo.demo.dto.UltimasVentasDTO;
import com.uade.tpo.demo.entity.Pedido;
import com.uade.tpo.demo.entity.Videojuego;
import com.uade.tpo.demo.repository.PedidoRepository;
import com.uade.tpo.demo.repository.VideojuegoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EstadisticasService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private VideojuegoRepository videojuegoRepository;

    public Double obtenerRecaudacionMensual() {
        YearMonth currentMonth = YearMonth.now();
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();
        LocalDateTime startOfMonthDateTime = startOfMonth.atStartOfDay();
        LocalDateTime endOfMonthDateTime = endOfMonth.atTime(23, 59, 59);
        return pedidoRepository.findAll().stream()
                .filter(p -> !p.getFecha().isBefore(startOfMonthDateTime) && !p.getFecha().isAfter(endOfMonthDateTime))
                .mapToDouble(Pedido::getMontoTotal)
                .sum();
    }

    public Double obtenerRecaudacionDiaria() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);
        return pedidoRepository.findAll().stream()
                .filter(p -> !p.getFecha().isBefore(startOfDay) && !p.getFecha().isAfter(endOfDay))
                .mapToDouble(Pedido::getMontoTotal)
                .sum();
    }

    public Map<String, Integer> obtenerVentasPorCategoria() {
        Map<String, Integer> ventasPorCategoria = new HashMap<>();
        videojuegoRepository.findAll().forEach(videojuego -> {
            String categoria = videojuego.getCategorias().toString();
            ventasPorCategoria.put(categoria, ventasPorCategoria.getOrDefault(categoria, 0) + videojuego.getVentas());
        });
        return ventasPorCategoria;
    }

    public List<UltimasVentasDTO> obtenerUltimasVentas() {
        return pedidoRepository.findTop10ByEstadoPedidoOrderByFechaDesc(Pedido.EstadoPedido.CONFIRMADO).stream()
                .map(pedido -> UltimasVentasDTO.builder()
                    .id(pedido.getId())
                    .fecha(pedido.getFecha())
                    .montoTotal(pedido.getMontoTotal())
                    .estadoPedido(pedido.getEstadoPedido().name())
                    .items(pedido.getProductosAdquiridos().stream()
                        .map(item -> new UltimasVentasDTO.ItemPedidoDTO(
                            item.getVideojuego().getTitulo(),
                            item.getCantidad()
                        ))
                        .collect(Collectors.toList()))
                    .build())
                .collect(Collectors.toList());
    }
    

    public List<ProductoMasVendidoDTO> obtenerProductosMasVendidos() {
        return videojuegoRepository.findTop5ByOrderByVentasDesc().stream()
            .map(videojuego -> new ProductoMasVendidoDTO(
                videojuego.getTitulo(),
                videojuego.getVentas(),
                videojuego.getFoto() != null ? "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(videojuego.getFoto()) : null,
                videojuego.getPlataforma()
            ))
            .collect(Collectors.toList());
    }
    

}