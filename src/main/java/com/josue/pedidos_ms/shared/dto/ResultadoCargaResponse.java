package com.josue.pedidos_ms.shared.dto;

import java.util.List;
import java.util.Map;

/**
 * DTO para la respuesta de carga de pedidos.
 * Proporciona una estructura consistente para todas las respuestas del
 * endpoint.
 */
public record ResultadoCargaResponse(
    int totalRegistros,
    int guardados,
    Map<String, List<Map<String, Object>>> errores,
    String requestId,
    String tiempoProcesamiento) {

  /**
   * Constructor sin información de request (para errores tempranos)
   */
  public ResultadoCargaResponse(int totalRegistros, int guardados, Map<String, List<Map<String, Object>>> errores) {
    this(totalRegistros, guardados, errores, null, null);
  }

  /**
   * Verifica si hay errores en el procesamiento
   */
  public boolean tieneErrores() {
    return errores != null && !errores.isEmpty();
  }

  /**
   * Cuenta el total de errores individuales
   */
  public int totalErrores() {
    if (errores == null)
      return 0;
    return errores.values().stream()
        .mapToInt(List::size)
        .sum();
  }
}
