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
      long inicioTiempo = System.currentTimeMillis();
      ResultadoCargaResponse resultado = cargarPedidosUseCase.procesarArchivo(file);
      long tiempoProcesamiento = System.currentTimeMillis() - inicioTiempo;

      // Crear respuesta completa con información de request
      ResultadoCargaResponse respuesta = new ResultadoCargaResponse(
          resultado.totalRegistros(),
          resultado.guardados(),
          resultado.errores(),
          requestId,
          tiempoProcesamiento + "ms");

      // Log del resultado
      logInfo(LogEvents.FIN_CARGA_CSV,
          "Request ID: {} - Procesamiento completado en {}ms - Total: {}, Guardados: {}, Tipos de errores: {}",
          requestId, tiempoProcesamiento, resultado.totalRegistros(), resultado.guardados(),
          resultado.errores().size());

      // Determinar código de estado HTTP basado en el resultado
      if (resultado.tieneErrores()) {
        logWarn(LogEvents.ESTADISTICAS_VALIDACION,
            "Request ID: {} - Errores de validación encontrados: {} errores totales",
            requestId, resultado.totalErrores());

        // HTTP 422: Datos válidos pero con errores de negocio
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(respuesta);
      }

      // HTTP 200: Todo procesado exitosamente
      return ResponseEntity.ok(respuesta);

    } finally {
      LogContext.clear();
    }
  }
}
