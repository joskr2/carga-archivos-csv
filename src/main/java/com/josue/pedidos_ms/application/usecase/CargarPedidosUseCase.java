package com.josue.pedidos_ms.application.usecase;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.josue.pedidos_ms.domain.model.*;
import com.josue.pedidos_ms.domain.service.*;
import com.josue.pedidos_ms.infrastructure.repository.*;
import com.josue.pedidos_ms.shared.dto.PedidoCsvDTO;
import com.josue.pedidos_ms.shared.dto.ResultadoCargaResponse;
import com.josue.pedidos_ms.shared.error.CsvValidationException;
import com.josue.pedidos_ms.shared.logging.BaseLogger;
import com.josue.pedidos_ms.shared.logging.LogContext;
import com.josue.pedidos_ms.shared.logging.LogEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CargarPedidosUseCase extends BaseLogger {

  private final PedidoRepository pedidoRepository;
  private final ClienteRepository clienteRepository;
  private final ZonaRepository zonaRepository;

  // Validadores específicos con logging
  private final PedidoValidator pedidoValidator;
  private final ClienteValidator clienteValidator;
  private final ZonaValidator zonaValidator;
  private final EstadoValidator estadoValidator;
  private final FechaValidator fechaValidator;

  public ResultadoCargaResponse procesarArchivo(MultipartFile file) {
    LogContext.setOperacion("CARGA_CSV");

    logInfo(LogEvents.INICIO_CARGA_CSV,
        "Iniciando procesamiento de archivo: {} (tamaño: {} bytes)",
        file.getOriginalFilename(), file.getSize());

    int total = 0;
    int guardados = 0;
    List<Pedido> pedidosValidos = new ArrayList<>();
    Map<String, List<Map<String, Object>>> erroresAgrupados = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        CSVReader csvReader = new CSVReader(reader)) {

      List<String[]> filas;

      try {
        filas = csvReader.readAll();
      } catch (IOException | CsvException e) {
        logError(LogEvents.ERROR_LECTURA_CSV,
            "Error al leer archivo CSV: {}", file.getOriginalFilename(), e);
        throw new CsvValidationException("Archivo CSV ilegible o mal formateado: " + e.getMessage(), e);
      }
      if (filas.size() <= 1) {
        logWarn(LogEvents.ARCHIVO_VACIO,
            "Archivo CSV vacío o solo con cabecera: {} filas", filas.size());
        LogContext.clear();
        return new ResultadoCargaResponse(
            0,
            0,
            Map.of("Archivo vacío", List.of(Map.of("linea", 0, "motivo", "El archivo no contiene datos"))));
      }

      logInfo(LogEvents.INICIO_CARGA_CSV,
          "Procesando {} filas de datos (excluyendo cabecera)", filas.size() - 1);

      for (int i = 1; i < filas.size(); i++) { // Ignora cabecera
        total++;
        String[] campos = filas.get(i);

        // Validar que la fila tenga todos los campos
        if (campos.length < 6) {
          String error = "Fila incompleta";
          erroresAgrupados
              .computeIfAbsent(error, k -> new ArrayList<>())
              .add(Map.of("linea", i + 1, "motivo", "Faltan campos en la fila"));
          continue;
        }

        PedidoCsvDTO dto = new PedidoCsvDTO(
            campos[0].trim(),
            campos[1].trim(),
            campos[2].trim(),
            campos[3].trim(),
            campos[4].trim(),
            campos[5].trim());

        // Establecer contexto del pedido actual
        LogContext.setPedido(dto.getNumeroPedido());

        List<String> errores = validar(dto);
        if (errores.isEmpty()) {
          Pedido pedido = convertirADominio(dto);
          pedidosValidos.add(pedido);
          logDebug(LogEvents.PEDIDO_PROCESADO,
              "Pedido válido agregado para guardado: {}", dto.getNumeroPedido());
        } else {
          logWarn(LogEvents.PEDIDO_INVALIDO,
              "Pedido con {} errores de validación", errores.size());
          for (String error : errores) {
            erroresAgrupados
                .computeIfAbsent(error, k -> new ArrayList<>())
                .add(Map.of("linea", i + 1, "motivo", error));
          }
        }
      }

      // Guardar todos los pedidos válidos
      if (!pedidosValidos.isEmpty()) {
        LogContext.setOperacion("PERSISTENCIA");
        logInfo(LogEvents.PEDIDO_GUARDADO,
            "Guardando {} pedidos válidos en base de datos", pedidosValidos.size());
        pedidoRepository.saveAll(pedidosValidos);
        guardados = pedidosValidos.size();
        logInfo(LogEvents.PEDIDO_GUARDADO,
            "Guardados exitosamente {} pedidos", guardados);
      }

    } catch (CsvValidationException e) {
      // Re-lanzar excepciones de CSV para manejo en el controlador
      throw e;
    } catch (Exception e) {
      logError(LogEvents.ERROR_LECTURA_CSV,
          "Error general al procesar archivo CSV", e);
      erroresAgrupados.put("Error general", List.of(Map.of("linea", 0, "motivo", e.getMessage())));
    } finally {
      LogContext.clear();
    }

    // Log de resumen final
    logInfo(LogEvents.RESUMEN_PROCESAMIENTO,
        "Procesamiento completado - Total: {}, Guardados: {}, Errores: {}",
        total, guardados, erroresAgrupados.size());

    return new ResultadoCargaResponse(total, guardados, erroresAgrupados);
  }

  private List<String> validar(PedidoCsvDTO dto) {
    List<String> errores = new ArrayList<>();

    logDebug(LogEvents.INICIO_VALIDACION,
        "Iniciando validación completa de pedido: {}", dto.getNumeroPedido());

    // Validaciones usando los validadores específicos
    errores.addAll(pedidoValidator.validarNumeroPedido(dto));
    errores.addAll(clienteValidator.validarCliente(dto));
    errores.addAll(fechaValidator.validarFecha(dto));
    errores.addAll(estadoValidator.validarEstado(dto));
    errores.addAll(zonaValidator.validarZona(dto));

    if (errores.isEmpty()) {
      logDebug(LogEvents.PEDIDO_VALIDO,
          "Todas las validaciones pasaron exitosamente");
    } else {
      logInfo(LogEvents.ESTADISTICAS_VALIDACION,
          "Validación completada con {} errores: {}", errores.size(), errores);
    }

    return errores;
  }

  private Pedido convertirADominio(PedidoCsvDTO dto) {
    LogContext.setOperacion("CONVERSION");

    logDebug(LogEvents.PEDIDO_PROCESADO,
        "Convirtiendo DTO a entidad de dominio: {}", dto.getNumeroPedido());

    try {
      Pedido pedido = Pedido.builder()
          .numeroPedido(dto.getNumeroPedido())
          .cliente(clienteRepository.findById(dto.getClienteId()).get())
          .fechaEntrega(LocalDate.parse(dto.getFechaEntrega()))
          .estado(EstadoPedido.valueOf(dto.getEstado()))
          .zonaEntrega(zonaRepository.findById(dto.getZonaEntrega()).get())
          .requiereRefrigeracion(Boolean.parseBoolean(dto.getRequiereRefrigeracion()))
          .build();

      logDebug(LogEvents.PEDIDO_PROCESADO,
          "Conversión exitosa - Pedido: {}, Cliente: {}, Fecha: {}, Estado: {}, Zona: {}, Refrigeración: {}",
          pedido.getNumeroPedido(),
          pedido.getCliente().getId(),
          pedido.getFechaEntrega(),
          pedido.getEstado(),
          pedido.getZonaEntrega().getId(),
          pedido.isRequiereRefrigeracion());

      return pedido;

    } catch (Exception e) {
      logError(LogEvents.ERROR_SISTEMA,
          "Error al convertir DTO a entidad de dominio", e);
      throw e;
    }
  }
}
