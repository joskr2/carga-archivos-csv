package com.josue.pedidos_ms.infrastructure.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para gestión manual de caché
 * Permite limpiar cachés específicos cuando sea necesario
 */
@Service
@Slf4j
public class CacheManagementService {

  /**
   * Limpia todos los cachés de clientes
   */
  @CacheEvict(value = { "clientes", "clientes-ordenados" }, allEntries = true)
  public void limpiarCacheClientes() {
    log.info("Cache de clientes limpiado manualmente");
  }

  /**
   * Limpia todos los cachés de zonas
   */
  @CacheEvict(value = { "zonas", "zonas-refrigeracion" }, allEntries = true)
  public void limpiarCacheZonas() {
    log.info("Cache de zonas limpiado manualmente");
  }

  /**
   * Limpia cache específico de un cliente
   */
  @CacheEvict(value = "clientes", key = "#clienteId")
  public void limpiarCacheCliente(String clienteId) {
    log.info("Cache del cliente {} limpiado", clienteId);
  }

  /**
   * Limpia cache específico de una zona
   */
  @CacheEvict(value = "zonas", key = "#zonaId")
  public void limpiarCacheZona(String zonaId) {
    log.info("Cache de la zona {} limpiado", zonaId);
  }

  /**
   * Limpia todos los cachés de estadísticas de pedidos
   */
  @CacheEvict(value = "estadisticas-pedidos", allEntries = true)
  public void limpiarCacheEstadisticasPedidos() {
    log.info("Cache de estadísticas de pedidos limpiado");
  }

  /**
   * Limpia todos los cachés del sistema
   */
  @CacheEvict(value = {
      "clientes",
      "clientes-ordenados",
      "zonas",
      "zonas-refrigeracion",
      "estadisticas-pedidos"
  }, allEntries = true)
  public void limpiarTodosLosCaches() {
    log.info("Todos los cachés del sistema han sido limpiados");
  }
}
