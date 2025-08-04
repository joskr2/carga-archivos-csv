package com.josue.pedidos_ms.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "zona", indexes = {
        @Index(name = "idx_zona_soporte_refrigeracion", columnList = "soporte_refrigeracion"),
        @Index(name = "idx_zona_id_soporte", columnList = "id, soporte_refrigeracion")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Zona {

    @Id
    @Column(name = "id", nullable = false, length = 50)
    private String id;

    @Column(name = "soporte_refrigeracion", nullable = false)
    private boolean soporteRefrigeracion;
}
