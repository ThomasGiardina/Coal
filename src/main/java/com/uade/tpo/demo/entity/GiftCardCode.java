package com.uade.tpo.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class GiftCardCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String code;

    @Column(nullable = false)
    private Double valor;

    @ManyToOne(optional = false)
    private Usuario usuario; // destinatario

    @ManyToOne(optional = false)
    private Pedido pedido; // pedido que lo generó

    @Column(nullable = false)
    private boolean utilizado = false;

    private LocalDateTime creadoEn = LocalDateTime.now();
    private LocalDateTime utilizadoEn;
}
