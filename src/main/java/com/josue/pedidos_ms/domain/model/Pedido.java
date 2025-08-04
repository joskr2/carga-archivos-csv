package com.josue.pedidos_ms.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "pedido", indexes = {
        @Index(name = "idx_pedido_estado", columnList = "estado"),
        @Index(name = "idx_pedido_fecha_entrega", columnList = "fecha_entrega"),
        @Index(name = "idx_pedido_cliente_id", columnList = "cliente_id"),
        @Index(name = "idx_pedido_zona_entrega", columnList = "zona_entrega"),
        @Index(name = "idx_pedido_requiere_refrigeracion", columnList = "requiere_refrigeracion"),
        @Index(name = "idx_pedido_estado_fecha", columnList = "estado, fecha_entrega"),
        @Index(name = "idx_pedido_cliente_fecha", columnList = "cliente_id, fecha_entrega"),
        @Index(name = "idx_pedido_refrigeracion_zona", columnList = "requiere_refrigeracion, zona_entrega")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @Column(name = "numero_pedido", nullable = false, length = 50)
    private String numeroPedido; // P001, P002, etc.

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "fecha_entrega", nullable = false)
    private LocalDate fechaEntrega;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoPedido estado;

    @ManyToOne(optional = false)
    @JoinColumn(name = "zona_entrega", nullable = false)
    private Zona zonaEntrega;

    @Column(name = "requiere_refrigeracion", nullable = false)
    private boolean requiereRefrigeracion;
}
