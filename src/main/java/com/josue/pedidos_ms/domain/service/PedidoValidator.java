package com.josue.pedidos_ms.domain.service;

import com.josue.pedidos_ms.infrastructure.repository.PedidoRepository;
import com.josue.pedidos_ms.shared.dto.PedidoCsvDTO;
import com.josue.pedidos_ms.shared.logging.BaseLogger;
import com.josue.pedidos_ms.shared.logging.LogContext;
import com.josue.pedidos_ms.shared.logging.LogEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Validador de números de pedido.
 * Verifica que los números sean únicos y no duplicados.
 */
@Service
@RequiredArgsConstructor
public class PedidoValidator extends BaseLogger {

  private final PedidoRepository pedidoRepository;

  /**
   * Valida que el número de pedido sea único en el sistema.
   * 
   * @param dto Datos del pedido CSV
   * @return Lista de errores encontrados (vacía si es válido)
   */
  public List<String> validarNumeroPedido(PedidoCsvDTO dto) {
    List<String> errores = new ArrayList<>();
    String numeroPedido = dto.getNumeroPedido();

    logInfo(LogEvents.INICIO_VALIDACION,
        "Validando número de pedido: {}", numeroPedido);

    // Establece contexto para los logs subsecuentes
    LogContext.setPedido(numeroPedido);

    try {
      if (numeroPedido == null || numeroPedido.trim().isEmpty()) {
        String error = "Número de pedido vacío";
        logWarn(LogEvents.PEDIDO_INVALIDO,
            "Número de pedido vacío o nulo");
        errores.add(error);
        return errores;
      }

      if (pedidoRepository.existsById(numeroPedido)) {
        String error = "Número de pedido duplicado";
        logWarn(LogEvents.PEDIDO_INVALIDO,
            "Número de pedido duplicado en base de datos: {}", numeroPedido);
        errores.add(error);
      } else {
        logDebug(LogEvents.PEDIDO_VALIDO,
            "Número de pedido único y válido: {}", numeroPedido);
      }

    } catch (Exception e) {
      String error = "Error al validar número de pedido";
      logError(LogEvents.ERROR_VALIDACION,
          "Error inesperado al validar número de pedido: {}",
          numeroPedido, e);
      errores.add(error);
    }

    return errores;
  }
}
