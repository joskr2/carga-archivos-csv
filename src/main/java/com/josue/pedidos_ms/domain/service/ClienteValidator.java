package com.josue.pedidos_ms.domain.service;

import com.josue.pedidos_ms.domain.model.Cliente;
import com.josue.pedidos_ms.infrastructure.repository.ClienteRepository;
import com.josue.pedidos_ms.shared.dto.PedidoCsvDTO;
import com.josue.pedidos_ms.shared.logging.BaseLogger;
import com.josue.pedidos_ms.shared.logging.LogEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Validador de clientes.
 * Verifica que los IDs de cliente existan en el sistema.
 */
@Service
@RequiredArgsConstructor
public class ClienteValidator extends BaseLogger {

  private final ClienteRepository clienteRepository;

  /**
   * Valida que el cliente exista en el sistema.
   * 
   * @param dto Datos del pedido CSV
   * @return Lista de errores encontrados (vacía si es válido)
   */
  public List<String> validarCliente(PedidoCsvDTO dto) {
    List<String> errores = new ArrayList<>();
    String clienteId = dto.getClienteId();

    logDebug(LogEvents.INICIO_VALIDACION,
        "Validando cliente: {}", clienteId);

    try {
      if (clienteId == null || clienteId.trim().isEmpty()) {
        String error = "ID de cliente vacío";
        logWarn(LogEvents.PEDIDO_INVALIDO,
            "ID de cliente vacío o nulo");
        errores.add(error);
        return errores;
      }

      Cliente cliente = clienteRepository.findById(clienteId).orElse(null);
      if (cliente == null) {
        String error = "Clientes no encontrados";
        logWarn(LogEvents.PEDIDO_INVALIDO,
            "Cliente no encontrado en base de datos: {}", clienteId);
        errores.add(error);
      } else {
        logDebug(LogEvents.PEDIDO_VALIDO,
            "Cliente válido encontrado: {} - {}", clienteId, cliente.getNombre());
      }

    } catch (Exception e) {
      String error = "Error al validar cliente";
      logError(LogEvents.ERROR_VALIDACION,
          "Error inesperado al validar cliente: {}", clienteId, e);
      errores.add(error);
    }

    return errores;
  }
}
