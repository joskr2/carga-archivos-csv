package com.josue.pedidos_ms.domain.service;

import com.josue.pedidos_ms.domain.model.Zona;
import com.josue.pedidos_ms.infrastructure.repository.ZonaRepository;
import com.josue.pedidos_ms.shared.dto.PedidoCsvDTO;
import com.josue.pedidos_ms.shared.logging.BaseLogger;
import com.josue.pedidos_ms.shared.logging.LogEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Validador de zonas de entrega.
 * Verifica que las zonas existan y tengan soporte para refrigeración cuando sea
 * necesario.
 */
@Service
@RequiredArgsConstructor
public class ZonaValidator extends BaseLogger {

  private final ZonaRepository zonaRepository;

  /**
   * Valida que la zona de entrega exista y tenga soporte para refrigeración si es
   * necesario.
   * 
   * @param dto Datos del pedido CSV
   * @return Lista de errores encontrados (vacía si es válido)
   */
  public List<String> validarZona(PedidoCsvDTO dto) {
    List<String> errores = new ArrayList<>();
    String zonaId = dto.getZonaEntrega();
    String requiereRefrigeracion = dto.getRequiereRefrigeracion();

    logDebug(LogEvents.INICIO_VALIDACION,
        "Validando zona: {} con refrigeración: {}", zonaId, requiereRefrigeracion);

    try {
      if (zonaId == null || zonaId.trim().isEmpty()) {
        String error = "Zona de entrega vacía";
        logWarn(LogEvents.PEDIDO_INVALIDO,
            "Zona de entrega vacía o nula");
        errores.add(error);
        return errores;
      }

      Zona zona = zonaRepository.buscarZona(zonaId).orElse(null);
      if (zona == null) {
        String error = "Zonas inválidas";
        logWarn(LogEvents.PEDIDO_INVALIDO,
            "Zona no encontrada en base de datos: {}", zonaId);
        errores.add(error);
        return errores;
      }

      // Validar soporte de refrigeración
      boolean requiereRefrig = "true".equalsIgnoreCase(requiereRefrigeracion);
      if (requiereRefrig && !zona.isSoporteRefrigeracion()) {
        String error = "Zona no soporta refrigeración";
        logWarn(LogEvents.PEDIDO_INVALIDO,
            "Zona {} no soporta refrigeración pero se requiere", zonaId);
        errores.add(error);
      } else {
        logDebug(LogEvents.PEDIDO_VALIDO,
            "Zona válida: {} - Soporte refrigeración: {} - Requerido: {}",
            zonaId, zona.isSoporteRefrigeracion(), requiereRefrig);
      }

    } catch (Exception e) {
      String error = "Error al validar zona";
      logError(LogEvents.ERROR_VALIDACION,
          "Error inesperado al validar zona: {}", zonaId, e);
      errores.add(error);
    }

    return errores;
  }
}
