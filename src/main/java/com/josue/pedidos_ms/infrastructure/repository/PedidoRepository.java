package com.josue.pedidos_ms.infrastructure.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.josue.pedidos_ms.domain.model.Pedido;
import com.josue.pedidos_ms.domain.model.EstadoPedido;

import java.time.LocalDate;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, String> {

    // ✅ JPQL explícitas con caché estratégico

    // ⚠️ NO usar caché para existencia (datos dinámicos)
    @Query("SELECT COUNT(p) > 0 FROM Pedido p WHERE p.numeroPedido = :numeroPedido")
    boolean existePedido(@Param("numeroPedido") String numeroPedido);

    @Query("SELECT p FROM Pedido p WHERE p.numeroPedido = :numeroPedido")
    Optional<Pedido> buscarPedido(@Param("numeroPedido") String numeroPedido);

    @Query("SELECT p FROM Pedido p JOIN FETCH p.cliente JOIN FETCH p.zonaEntrega WHERE p.estado = :estado ORDER BY p.fechaEntrega ASC")
    java.util.List<Pedido> buscarPedidosPorEstadoConDetalles(@Param("estado") EstadoPedido estado);

    @Query("SELECT p FROM Pedido p WHERE p.fechaEntrega BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaEntrega ASC")
    java.util.List<Pedido> buscarPedidosPorRangoFechas(@Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    @Cacheable(value = "estadisticas-pedidos", key = "#clienteId")
    @Query("SELECT p FROM Pedido p JOIN FETCH p.cliente c JOIN FETCH p.zonaEntrega z WHERE c.id = :clienteId ORDER BY p.fechaEntrega DESC")
    java.util.List<Pedido> buscarPedidosDeClienteConDetalles(@Param("clienteId") String clienteId);

    @Cacheable(value = "estadisticas-pedidos", key = "'conflictos-refrigeracion'")
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.requiereRefrigeracion = true AND p.zonaEntrega.soporteRefrigeracion = false")
    long contarPedidosConConflictoRefrigeracion();

    // 💡 Implementaciones anteriores con métodos derivados (comentadas)
    // boolean existsByNumeroPedido(String numeroPedido);
    // Optional<Pedido> findByNumeroPedido(String numeroPedido);
    // List<Pedido> findByEstadoOrderByFechaEntregaAsc(EstadoPedido estado);
    // List<Pedido> findByFechaEntregaBetweenOrderByFechaEntregaAsc(LocalDate
    // fechaInicio, LocalDate fechaFin);
    // List<Pedido> findByClienteIdOrderByFechaEntregaDesc(String clienteId);
    // long
    // countByRequiereRefrigeracionTrueAndZonaEntregaSoporteRefrigeracionFalse();
}
