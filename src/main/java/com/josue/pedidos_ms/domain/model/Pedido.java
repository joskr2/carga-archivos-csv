package com.josue.pedidos_ms.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    private String numeroPedido; // P001, P002, etc.

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    private LocalDate fechaEntrega;

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    @ManyToOne(optional = false)
    @JoinColumn(name = "zona_entrega")
    private Zona zonaEntrega;

    private boolean requiereRefrigeracion;
}
