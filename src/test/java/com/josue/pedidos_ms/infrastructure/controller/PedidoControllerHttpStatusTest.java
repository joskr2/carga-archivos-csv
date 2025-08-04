package com.josue.pedidos_ms.infrastructure.controller;

import com.josue.pedidos_ms.domain.model.Cliente;
import com.josue.pedidos_ms.domain.model.Zona;
import com.josue.pedidos_ms.infrastructure.repository.ClienteRepository;
import com.josue.pedidos_ms.infrastructure.repository.ZonaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PedidoControllerHttpStatusTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ClienteRepository clienteRepository;

  @Autowired
  private ZonaRepository zonaRepository;

  @BeforeEach
  void setUp() {
    // Limpiar datos existentes
    clienteRepository.deleteAll();
    zonaRepository.deleteAll();

    // Insertar datos en H2 para las validaciones
    clienteRepository.save(new Cliente("CLI-123", "Test Cliente"));
    zonaRepository.save(new Zona("ZONA1", true));
  }

  @Test
  void debeRetornar200CuandoTodosLosPedidosSonValidos() throws Exception {
    String csv = "numeroPedido,clienteId,fechaEntrega,estado,zonaEntrega,requiereRefrigeracion\n" +
        "P001,CLI-123,2099-08-10,PENDIENTE,ZONA1,true\n";

    MockMultipartFile file = new MockMultipartFile(
        "file", "pedidos.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

    mockMvc.perform(multipart("/pedidos/cargar").file(file))
        .andExpect(status().isOk()) // 200 OK
        .andExpect(jsonPath("$.totalRegistros").value(1))
        .andExpect(jsonPath("$.guardados").value(1))
        .andExpect(jsonPath("$.errores").isEmpty())
        .andExpect(jsonPath("$.requestId").exists())
        .andExpect(jsonPath("$.tiempoProcesamiento").exists());
  }

  @Test
  void debeRetornar422CuandoHayErroresDeValidacion() throws Exception {
    String csv = "numeroPedido,clienteId,fechaEntrega,estado,zonaEntrega,requiereRefrigeracion\n" +
        "P001,CLI-INEXISTENTE,2020-01-01,ESTADO_INVALIDO,ZONA_INEXISTENTE,true\n";

    MockMultipartFile file = new MockMultipartFile(
        "file", "pedidos.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

    mockMvc.perform(multipart("/pedidos/cargar").file(file))
        .andExpect(status().isUnprocessableEntity()) // 422 UNPROCESSABLE_ENTITY
        .andExpect(jsonPath("$.totalRegistros").value(1))
        .andExpect(jsonPath("$.guardados").value(0))
        .andExpect(jsonPath("$.errores").isNotEmpty())
        .andExpect(jsonPath("$.requestId").exists())
        .andExpect(jsonPath("$.tiempoProcesamiento").exists());
  }

  @Test
  void debeRetornar400CuandoElArchivoEstaVacio() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file", "pedidos.csv", "text/csv", "".getBytes(StandardCharsets.UTF_8));

    mockMvc.perform(multipart("/pedidos/cargar").file(file))
        .andExpect(status().isBadRequest()) // 400 BAD_REQUEST
        .andExpect(jsonPath("$.totalRegistros").value(0))
        .andExpect(jsonPath("$.guardados").value(0))
        .andExpect(jsonPath("$.errores.csvInvalido").exists());
  }

  @Test
  void debeRetornar400CuandoElArchivoNoEsCsv() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file", "pedidos.txt", "text/plain", "contenido".getBytes(StandardCharsets.UTF_8));

    mockMvc.perform(multipart("/pedidos/cargar").file(file))
        .andExpect(status().isBadRequest()) // 400 BAD_REQUEST
        .andExpect(jsonPath("$.totalRegistros").value(0))
        .andExpect(jsonPath("$.guardados").value(0))
        .andExpect(jsonPath("$.errores.csvInvalido").exists());
  }

  @Test
  void debeRetornar400CuandoElCsvEsMalformado() throws Exception {
    // CSV con formato inválido (comillas mal cerradas)
    String csvMalformado = "numeroPedido,clienteId,fechaEntrega\n" +
        "\"P001,CLI-123,2099-08-10\n"; // Comilla sin cerrar

    MockMultipartFile file = new MockMultipartFile(
        "file", "pedidos.csv", "text/csv", csvMalformado.getBytes(StandardCharsets.UTF_8));

    mockMvc.perform(multipart("/pedidos/cargar").file(file))
        .andExpect(status().isBadRequest()) // 400 BAD_REQUEST
        .andExpect(jsonPath("$.errores.csvInvalido").exists());
  }

  @Test
  void debeRetornar422CuandoHayMezclaDeValidosEInvalidos() throws Exception {
    String csv = "numeroPedido,clienteId,fechaEntrega,estado,zonaEntrega,requiereRefrigeracion\n" +
        "P001,CLI-123,2099-08-10,PENDIENTE,ZONA1,true\n" +
        "P002,CLI-INEXISTENTE,2020-01-01,INVALIDO,ZONA_INEXISTENTE,false\n";

    MockMultipartFile file = new MockMultipartFile(
        "file", "pedidos.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

    mockMvc.perform(multipart("/pedidos/cargar").file(file))
        .andExpect(status().isUnprocessableEntity()) // 422 UNPROCESSABLE_ENTITY
        .andExpect(jsonPath("$.totalRegistros").value(2))
        .andExpect(jsonPath("$.guardados").value(1)) // Solo uno válido
        .andExpect(jsonPath("$.errores").isNotEmpty()) // Hay errores
        .andExpect(jsonPath("$.requestId").exists())
        .andExpect(jsonPath("$.tiempoProcesamiento").exists());
  }
}
