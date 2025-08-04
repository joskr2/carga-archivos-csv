package com.josue.pedidos_ms.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cliente", indexes = {
        @Index(name = "idx_cliente_nombre", columnList = "nombre"),
        @Index(name = "idx_cliente_id_nombre", columnList = "id, nombre")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @Column(name = "id", nullable = false, length = 50)
    private String id;

    @Column(name = "nombre", length = 200)
    private String nombre;
}
