package com.josue.pedidos_ms.shared.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO para la respuesta de carga de pedidos.
 * Proporciona una estructura consistente para todas las respuestas del
 * endpoint.
 */
public record ResultadoCargaResponse(
    @JsonProperty("totalRegistros") int totalRegistros,

    @JsonProperty("registrosGuardados") int registrosGuardados,

    @JsonProperty("errores") List<ErrorDetalle> errores,

    @JsonProperty("requestId") String requestId,

    @JsonProperty("tiempoProcesamiento") long tiempoProcesamiento) {

  /**
   * Constructor sin información de request (para errores tempranos)
   */
  public ResultadoCargaResponse(int totalRegistros, int registrosGuardados, List<ErrorDetalle> errores) {
    this(totalRegistros, registrosGuardados, errores, null, 0);
  }

  /**
   * Verifica si hay errores en el procesamiento
   */
  public boolean tieneErrores() {
    return errores != null && !errores.isEmpty();
  }

  /**
   * DTO para los detalles de cada error encontrado
   */
  public record ErrorDetalle(
      @JsonProperty("linea") int linea,

      @JsonProperty("numeroPedido") String numeroPedido,

      @JsonProperty("motivo") String motivo,

      @JsonProperty("tipo") String tipo) {
  }
}
