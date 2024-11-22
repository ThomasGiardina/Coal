package com.uade.tpo.demo.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Data
@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID del pedido

    @ManyToOne
    @JoinColumn(name = "id_comprador", nullable = false)
    @JsonBackReference
    private Usuario comprador; // Relación con Usuario (ID Comprador)

    @Column(nullable = false)
    private String nombreComprador; // Nombre del comprador (obtenido de Usuario)

    @Column(nullable = false)
    private String usuarioComprador; // Username del comprador (obtenido de Usuario)

    @ManyToOne
    @JoinColumn(name = "id_metodo_pago", nullable = false)
    private MetodoPago metodoPago; // Relación con MetodoPago (ID Método Pago)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPago.TipoPago tipoPago; // Tipo de pago (EFECTIVO, DEBITO, CREDITO)

    @Column(nullable = false)
    private LocalDateTime fecha; // Fecha del pedido

    @Column(nullable = false)
    private Double montoTotal; // Monto total del pedido

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estadoPedido; // Estado del pedido (PENDIENTE, CONFIRMADO, CANCELADO)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 255) 
    private TipoEntrega tipoEntrega; // Tipo de entrega (Delivery o RetiroLocal)

    @Column(name = "direccion_envio")
    private String direccionEnvio;

    @Column(nullable = false)
    private int cantidadArticulos; // Cantidad de artículos en el pedido

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "pedido_id")
    @JsonManagedReference
    private List<ItemPedido> productosAdquiridos; // Lista de productos adquiridos en el pedido

    public enum EstadoPedido {
        PENDIENTE,
        CONFIRMADO,
        CANCELADO
    }

    public enum TipoEntrega {
        ENVIO,
        DELIVERY
    }
}
