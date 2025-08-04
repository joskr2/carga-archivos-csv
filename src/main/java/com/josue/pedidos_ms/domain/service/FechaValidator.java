package com.josue.pedidos_ms.domain.service;

import com.josue.pedidos_ms.shared.dto.PedidoCsvDTO;
import com.josue.pedidos_ms.shared.logging.BaseLogger;
import com.josue.pedidos_ms.shared.logging.LogEvents;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Validador de fechas de entrega.
 * Verifica que las fechas sean válidas y no sean del pasado.
 */
@Service
public class FechaValidator extends BaseLogger {

  private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * Valida que la fecha de entrega sea válida y no esté en el pasado.
   * 
   * @param dto Datos del pedido CSV
   * @return Lista de errores encontrados (vacía si es válido)
   */
  public List<String> validarFecha(PedidoCsvDTO dto) {
    List<String> errores = new ArrayList<>();
    String fechaStr = dto.getFechaEntrega();

    logDebug(LogEvents.INICIO_VALIDACION,
        "Validando fecha de entrega: {}", fechaStr);

    try {
      if (fechaStr == null || fechaStr.trim().isEmpty()) {
        String error = "Fecha de entrega vacía";
        logWarn(LogEvents.PEDIDO_INVALIDO,
            "Fecha de entrega vacía o nula");
        errores.add(error);
        return errores;
      }

      // Intenta parsear la fecha
      LocalDate fechaEntrega = LocalDate.parse(fechaStr, FORMATO_FECHA);
      LocalDate hoy = LocalDate.now();

      if (fechaEntrega.isBefore(hoy)) {
        String error = "Fecha de entrega inválida";
        logWarn(LogEvents.PEDIDO_INVALIDO,
            "Fecha de entrega en el pasado: {} (hoy: {})", fechaEntrega, hoy);
        errores.add(error);
      } else {
        logDebug(LogEvents.PEDIDO_VALIDO,
            "Fecha de entrega válida: {} (días desde hoy: {})",
            fechaEntrega, fechaEntrega.toEpochDay() - hoy.toEpochDay());
      }

    } catch (DateTimeParseException e) {
      String error = "Formato de fecha inválido";
      logWarn(LogEvents.PEDIDO_INVALIDO,
          "Formato de fecha inválido: {}. Formato esperado: yyyy-MM-dd", fechaStr);
      errores.add(error);
    } catch (Exception e) {
      String error = "Error al validar fecha";
      logError(LogEvents.ERROR_VALIDACION,
          "Error inesperado al validar fecha: {}", fechaStr, e);
      errores.add(error);
    }

    return errores;
  }
}
