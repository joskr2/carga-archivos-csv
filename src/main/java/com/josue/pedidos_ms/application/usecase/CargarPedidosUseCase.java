package com.josue.pedidos_ms.application.usecase;

import com.opencsv.CSVReader;
import com.josue.pedidos_ms.domain.model.*;
import com.josue.pedidos_ms.infrastructure.repository.*;
import com.josue.pedidos_ms.shared.dto.PedidoCsvDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CargarPedidosUseCase {

  private final PedidoRepository pedidoRepository;
  private final ClienteRepository clienteRepository;
  private final ZonaRepository zonaRepository;

  public Map<String, Object> procesarArchivo(MultipartFile file) {
    int total = 0;
    int guardados = 0;
    List<Pedido> pedidosValidos = new ArrayList<>();
    Map<String, List<Map<String, Object>>> erroresAgrupados = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        CSVReader csvReader = new CSVReader(reader)) {

      List<String[]> filas = csvReader.readAll();
      for (int i = 1; i < filas.size(); i++) { // Ignora cabecera
        total++;
        String[] campos = filas.get(i);
        PedidoCsvDTO dto = new PedidoCsvDTO(
            campos[0].trim(),
            campos[1].trim(),
            campos[2].trim(),
            campos[3].trim(),
            campos[4].trim(),
            campos[5].trim());

        List<String> errores = validar(dto);
        if (errores.isEmpty()) {
          Pedido pedido = convertirADominio(dto);
          pedidosValidos.add(pedido);
        } else {
          for (String error : errores) {
            erroresAgrupados
                .computeIfAbsent(error, k -> new ArrayList<>())
                .add(Map.of("linea", i + 1, "motivo", error));
          }
        }
      }

      pedidoRepository.saveAll(pedidosValidos);
      guardados = pedidosValidos.size();

    } catch (Exception e) {
      erroresAgrupados.put("Error general", List.of(Map.of("linea", 0, "motivo", e.getMessage())));
    }

    return Map.of(
        "totalRegistros", total,
        "guardados", guardados,
        "errores", erroresAgrupados);
  }

  private List<String> validar(PedidoCsvDTO dto) {
    List<String> errores = new ArrayList<>();

    // numeroPedido único
    if (pedidoRepository.existsById(dto.getNumeroPedido())) {
      errores.add("Número de pedido duplicado");
    }

    // clienteId debe existir
    Cliente cliente = clienteRepository.findById(dto.getClienteId()).orElse(null);
    if (cliente == null) {
      errores.add("Clientes no encontrados");
    }

    // fechaEntrega no puede ser pasada
    try {
      LocalDate fecha = LocalDate.parse(dto.getFechaEntrega());
      if (fecha.isBefore(LocalDate.now())) {
        errores.add("Fecha de entrega inválida");
      }
    } catch (Exception e) {
      errores.add("Formato de fecha inválido");
    }

    // estado válido
    try {
      EstadoPedido.valueOf(dto.getEstado());
    } catch (Exception e) {
      errores.add("Estado inválido");
    }

    // zonaEntrega debe existir
    Zona zona = zonaRepository.findById(dto.getZonaEntrega()).orElse(null);
    if (zona == null) {
      errores.add("Zonas inválidas");
    }

    // requiereRefrigeracion → zona debe soportarlo
    if ("true".equalsIgnoreCase(dto.getRequiereRefrigeracion()) && zona != null && !zona.isSoporteRefrigeracion()) {
      errores.add("Zona no soporta refrigeración");
    }

    return errores;
  }

  private Pedido convertirADominio(PedidoCsvDTO dto) {
    return Pedido.builder()
        .numeroPedido(dto.getNumeroPedido())
        .cliente(clienteRepository.findById(dto.getClienteId()).get())
        .fechaEntrega(LocalDate.parse(dto.getFechaEntrega()))
        .estado(EstadoPedido.valueOf(dto.getEstado()))
        .zonaEntrega(zonaRepository.findById(dto.getZonaEntrega()).get())
        .requiereRefrigeracion(Boolean.parseBoolean(dto.getRequiereRefrigeracion()))
        .build();
  }
}
