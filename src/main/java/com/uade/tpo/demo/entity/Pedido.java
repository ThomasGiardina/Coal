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
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_comprador", nullable = false)
    @JsonBackReference
    private Usuario comprador;

    @Column(nullable = false)
    private String nombreComprador;

    @Column(nullable = false)
    private String usuarioComprador;

    @ManyToOne
    @JoinColumn(name = "id_metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPago.TipoPago tipoPago;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private Double montoTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estadoPedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 255) 
    private TipoEntrega tipoEntrega;

    @Column(name = "direccion_envio")
    private String direccionEnvio;

    @Column(nullable = false)
    private int cantidadArticulos;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "pedido_id")
    @JsonManagedReference
    private List<ItemPedido> productosAdquiridos;

    public enum EstadoPedido {
        PENDIENTE,
        CONFIRMADO,
        CANCELADO
    }

    public enum TipoEntrega {
        ENVIO,
        RETIRO
    }
}
