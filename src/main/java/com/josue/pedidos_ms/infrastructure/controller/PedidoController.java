package com.josue.pedidos_ms.infrastructure.controller;

import com.josue.pedidos_ms.application.usecase.CargarPedidosUseCase;
import com.josue.pedidos_ms.shared.dto.ResultadoCargaResponse;
import com.josue.pedidos_ms.shared.error.CsvValidationException;
import com.josue.pedidos_ms.shared.logging.BaseLogger;
import com.josue.pedidos_ms.shared.logging.LogContext;
import com.josue.pedidos_ms.shared.logging.LogEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController extends BaseLogger {

  private final CargarPedidosUseCase cargarPedidosUseCase;

  @PostMapping(value = "/cargar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ResultadoCargaResponse> cargarArchivo(@RequestParam("file") MultipartFile file) {

    // Generar ID único para el request
    String requestId = UUID.randomUUID().toString().substring(0, 8);
    LogContext.setOperacion("HTTP_REQUEST");

    logInfo(LogEvents.INICIO_CARGA_CSV,
        "POST /pedidos/cargar - Request ID: {} - Archivo: {} - Tamaño: {} bytes",
        requestId, file.getOriginalFilename(), file.getSize());

    try {
      // Validaciones básicas del archivo
      if (file.isEmpty()) {
        logWarn(LogEvents.ARCHIVO_VACIO,
            "Request ID: {} - Archivo vacío recibido", requestId);
        throw new CsvValidationException("El archivo está vacío");
      }

      String filename = file.getOriginalFilename();
      if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
        logWarn(LogEvents.ERROR_LECTURA_CSV,
            "Request ID: {} - Formato de archivo inválido: {}",
            requestId, filename);
        throw new CsvValidationException("Solo se permiten archivos CSV");
      }

      // Procesar archivo
      ResultadoCargaResponse resultado = cargarPedidosUseCase.procesarArchivo(file);

      // Log del resultado
      logInfo(LogEvents.FIN_CARGA_CSV,
          "Request ID: {} - Procesamiento completado en {}ms - Total: {}, Guardados: {}, Errores: {}",
          requestId, resultado.tiempoProcesamiento(), resultado.totalRegistros(), resultado.registrosGuardados(),
          resultado.errores().size());

      // Determinar código de estado HTTP basado en el resultado
      if (resultado.tieneErrores() && resultado.registrosGuardados() == 0) {
        // Ningún registro fue guardado (todos tienen errores)
        logWarn(LogEvents.ESTADISTICAS_VALIDACION,
            "Request ID: {} - Ningún registro válido encontrado - {} errores totales",
            requestId, resultado.errores().size());

        // HTTP 422: Datos con errores de validación, nada guardado
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(resultado);
      }

      if (resultado.tieneErrores() && resultado.registrosGuardados() > 0) {
        // Algunos registros fueron guardados, otros tienen errores (procesamiento
        // parcial)
        logWarn(LogEvents.ESTADISTICAS_VALIDACION,
            "Request ID: {} - Procesamiento parcial - Guardados: {}, Errores: {}",
            requestId, resultado.registrosGuardados(), resultado.errores().size());

        // HTTP 200: Procesamiento parcial exitoso
        return ResponseEntity.ok(resultado);
      }

      // HTTP 200: Todo procesado exitosamente
      return ResponseEntity.ok(resultado);

    } finally {
      LogContext.clear();
    }
  }
}
