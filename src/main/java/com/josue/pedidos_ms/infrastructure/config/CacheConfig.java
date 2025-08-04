package com.josue.pedidos_ms.infrastructure.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de caché para optimizar consultas frecuentes
 * 
 * Estrategia de caché:
 * - clientes: Cache de larga duración para entidades de referencia
 * - zonas: Cache de larga duración para entidades de configuración
 * - pedidos-existencia: Cache de corta duración para validaciones
 * - estadisticas: Cache temporal para consultas de análisis
 */
@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public CacheManager cacheManager() {
    ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();

    // Configurar nombres de cache
    cacheManager.setCacheNames(java.util.Arrays.asList(
        "clientes", // Cache para entidades Cliente
        "zonas", // Cache para entidades Zona
        "pedidos-existencia", // Cache para validaciones de existencia
        "clientes-ordenados", // Cache para listas ordenadas
        "zonas-refrigeracion", // Cache para consultas de refrigeración
        "estadisticas-pedidos" // Cache para consultas de análisis
    ));

    // Permitir valores null en cache
    cacheManager.setAllowNullValues(true);

    return cacheManager;
  }
}
