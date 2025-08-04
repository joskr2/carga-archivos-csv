package com.josue.pedidos_ms.infrastructure.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.josue.pedidos_ms.domain.model.Zona;

import java.util.Optional;

public interface ZonaRepository extends JpaRepository<Zona, String> {

  // ✅ JPQL explícitas con caché para mayor rendimiento

  @Cacheable(value = "zonas", key = "#id")
  @Query("SELECT COUNT(z) > 0 FROM Zona z WHERE z.id = :id")
  boolean existsZona(@Param("id") String id);

  @Cacheable(value = "zonas", key = "#id")
  @Query("SELECT z FROM Zona z WHERE z.id = :id")
  Optional<Zona> buscarZona(@Param("id") String id);

  @Cacheable(value = "zonas-refrigeracion", key = "'con-refrigeracion'")
  @Query("SELECT z FROM Zona z WHERE z.soporteRefrigeracion = true ORDER BY z.id ASC")
  java.util.List<Zona> buscarZonasConRefrigeracion();

  @Cacheable(value = "zonas-refrigeracion", key = "#soporteRefrigeracion")
  @Query("SELECT z FROM Zona z WHERE z.soporteRefrigeracion = :soporteRefrigeracion ORDER BY z.id ASC")
  java.util.List<Zona> buscarZonasPorSoporteRefrigeracion(@Param("soporteRefrigeracion") boolean soporteRefrigeracion);

  // 💡 Implementaciones anteriores con métodos derivados (comentadas)
  // boolean existsById(String id);
  // Optional<Zona> findById(String id);
  // List<Zona> findBySoporteRefrigeracionTrueOrderByIdAsc();
  // List<Zona> findBySoporteRefrigeracionOrderByIdAsc(boolean
  // soporteRefrigeracion);
}
