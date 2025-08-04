package com.josue.pedidos_ms.domain.service;

import com.josue.pedidos_ms.domain.model.EstadoPedido;
import com.josue.pedidos_ms.shared.dto.PedidoCsvDTO;
import com.josue.pedidos_ms.shared.logging.BaseLogger;
import com.josue.pedidos_ms.shared.logging.LogEvents;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Validador de estados de pedido.
 * Verifica que el estado sea uno de los valores válidos del enum EstadoPedido.
 */
@Service
public class EstadoValidator extends BaseLogger {

  /**
   * Valida que el estado del pedido sea uno de los valores válidos.
   * 
   * @param dto Datos del pedido CSV
   * @return Lista de errores encontrados (vacía si es válido)
   */
  public List<String> validarEstado(PedidoCsvDTO dto) {
    List<String> errores = new ArrayList<>();
    String estado = dto.getEstado();

    logDebug(LogEvents.INICIO_VALIDACION,
        "Validando estado: {}", estado);

    try {
      if (estado == null || estado.trim().isEmpty()) {
        String error = "Estado vacío";
        logWarn(LogEvents.PEDIDO_INVALIDO,
            "Estado vacío o nulo");
        errores.add(error);
        return errores;
      }

      // Intenta convertir el string al enum
      EstadoPedido estadoPedido = EstadoPedido.valueOf(estado.toUpperCase());

      logDebug(LogEvents.PEDIDO_VALIDO,
          "Estado válido: {}", estadoPedido);

    } catch (IllegalArgumentException e) {
      String error = "Estado inválido";
      logWarn(LogEvents.PEDIDO_INVALIDO,
          "Estado inválido: {}. Estados válidos: PENDIENTE, CONFIRMADO, ENTREGADO",
          estado);
      errores.add(error);
    } catch (Exception e) {
      String error = "Error al validar estado";
      logError(LogEvents.ERROR_VALIDACION,
          "Error inesperado al validar estado: {}", estado, e);
      errores.add(error);
    }

    return errores;
  }
}
