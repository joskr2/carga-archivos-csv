package com.josue.pedidos_ms.infrastructure.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.josue.pedidos_ms.application.usecase.CargarPedidosUseCase;

import java.util.Map;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

  private final CargarPedidosUseCase cargarPedidosUseCase;

  @PostMapping(value = "/cargar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, Object>> cargarArchivo(@RequestParam("file") MultipartFile file) {
    Map<String, Object> resultado = cargarPedidosUseCase.procesarArchivo(file);
    return ResponseEntity.ok(resultado);
  }
}
