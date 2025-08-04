package com.josue.pedidos_ms.infrastructure.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.josue.pedidos_ms.domain.model.Cliente;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, String> {

  // ✅ JPQL explícitas con caché para mayor rendimiento

  @Cacheable(value = "clientes", key = "#id")
  @Query("SELECT COUNT(c) > 0 FROM Cliente c WHERE c.id = :id")
  boolean existsCliente(@Param("id") String id);

  @Cacheable(value = "clientes", key = "#id")
  @Query("SELECT c FROM Cliente c WHERE c.id = :id")
  Optional<Cliente> buscarCliente(@Param("id") String id);

  @Cacheable(value = "clientes-ordenados", key = "'todos'")
  @Query("SELECT c FROM Cliente c ORDER BY c.nombre ASC")
  java.util.List<Cliente> buscarTodosOrdenados();

  // 💡 Implementaciones anteriores con métodos derivados (comentadas)
  // boolean existsById(String id);
  // Optional<Cliente> findById(String id);
  // List<Cliente> findAllByOrderByNombreAsc();
}
