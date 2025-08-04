package com.josue.pedidos_ms.infrastructure.controller;

import com.josue.pedidos_ms.application.usecase.CargarPedidosUseCase;
import com.josue.pedidos_ms.shared.logging.BaseLogger;
import com.josue.pedidos_ms.shared.logging.LogContext;
import com.josue.pedidos_ms.shared.logging.LogEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController extends BaseLogger {

  private final CargarPedidosUseCase cargarPedidosUseCase;

  @PostMapping(value = "/cargar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, Object>> cargarArchivo(@RequestParam("file") MultipartFile file) {

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
        return ResponseEntity.badRequest()
            .body(Map.of("error", "El archivo está vacío"));
      }

      String filename = file.getOriginalFilename();
      if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
        logWarn(LogEvents.ERROR_LECTURA_CSV,
            "Request ID: {} - Formato de archivo inválido: {}",
            requestId, filename);
        return ResponseEntity.badRequest()
            .body(Map.of("error", "Solo se permiten archivos CSV"));
      }

      // Procesar archivo
      long inicioTiempo = System.currentTimeMillis();
      Map<String, Object> resultado = cargarPedidosUseCase.procesarArchivo(file);
      long tiempoProcesamiento = System.currentTimeMillis() - inicioTiempo;

      // Log del resultado
      int totalRegistros = (Integer) resultado.get("totalRegistros");
      int guardados = (Integer) resultado.get("guardados");
      Map<?, ?> errores = (Map<?, ?>) resultado.get("errores");

      logInfo(LogEvents.FIN_CARGA_CSV,
          "Request ID: {} - Procesamiento completado en {}ms - Total: {}, Guardados: {}, Tipos de errores: {}",
          requestId, tiempoProcesamiento, totalRegistros, guardados, errores.size());

      // Crear un nuevo Map mutable y agregar información del request
      Map<String, Object> respuesta = new HashMap<>(resultado);
      respuesta.put("requestId", requestId);
      respuesta.put("tiempoProcesamiento", tiempoProcesamiento + "ms");

      return ResponseEntity.ok(respuesta);

    } catch (Exception e) {
      logError(LogEvents.ERROR_SISTEMA,
          "Request ID: {} - Error inesperado al procesar archivo", requestId, e);

      return ResponseEntity.internalServerError()
          .body(Map.of(
              "error", "Error interno del servidor",
              "requestId", requestId,
              "motivo", e.getMessage()));

    } finally {
      LogContext.clear();
    }
  }
}
