package com.josue.pedidos_ms.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.josue.pedidos_ms.domain.model.Zona;

public interface ZonaRepository extends JpaRepository<Zona, String> {
}
