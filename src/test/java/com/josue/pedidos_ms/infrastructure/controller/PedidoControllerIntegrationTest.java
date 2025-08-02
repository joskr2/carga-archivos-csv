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
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PedidoControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ClienteRepository clienteRepository;

  @Autowired
  private ZonaRepository zonaRepository;

  @BeforeEach
  void setUp() {
    // Insertar datos en H2 para las validaciones
    clienteRepository.save(new Cliente("CLI-123", "Test Cliente"));
    zonaRepository.save(new Zona("ZONA1", true));
  }

  @Test
  void cargarCsvConUnPedidoValidoDebeResponderCorrectamente() throws Exception {
    String csv = "numeroPedido,clienteId,fechaEntrega,estado,zonaEntrega,requiereRefrigeracion\n" +
        "P001,CLI-123,2099-08-10,PENDIENTE,ZONA1,true\n";

    MockMultipartFile file = new MockMultipartFile(
        "file", "pedidos.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

    mockMvc.perform(multipart("/pedidos/cargar")
        .file(file))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalRegistros").value(1))
        .andExpect(jsonPath("$.guardados").value(1))
        .andExpect(jsonPath("$.errores").isEmpty());
  }
}
