package com.josue.pedidos_ms.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.josue.pedidos_ms.domain.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, String> {
    boolean existsByNumeroPedido(String numeroPedido);
}
