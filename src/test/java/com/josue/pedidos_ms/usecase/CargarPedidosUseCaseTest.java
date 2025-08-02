package com.josue.pedidos_ms.usecase;

import com.josue.pedidos_ms.application.usecase.CargarPedidosUseCase;
import com.josue.pedidos_ms.domain.model.*;
import com.josue.pedidos_ms.infrastructure.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CargarPedidosUseCaseTest {

  private PedidoRepository pedidoRepository;
  private ClienteRepository clienteRepository;
  private ZonaRepository zonaRepository;
  private CargarPedidosUseCase useCase;

  @BeforeEach
  void setUp() {
    pedidoRepository = mock(PedidoRepository.class);
    clienteRepository = mock(ClienteRepository.class);
    zonaRepository = mock(ZonaRepository.class);
    useCase = new CargarPedidosUseCase(pedidoRepository, clienteRepository, zonaRepository);
  }

  @Test
  void debeProcesarArchivoYGuardarPedidosValidos() {
    // CSV de prueba (2 líneas: 1 válida, 1 inválida)
    String csv = "numeroPedido,clienteId,fechaEntrega,estado,zonaEntrega,requiereRefrigeracion\n" +
        "P001,CLI-123,2099-08-10,PENDIENTE,ZONA1,true\n" +
        "P002,CLI-999,2020-01-01,INVALIDO,ZONA2,false\n";

    MockMultipartFile file = new MockMultipartFile(
        "file",
        "pedidos.csv",
        "text/csv",
        csv.getBytes(StandardCharsets.UTF_8));

    // Setup mocks
    when(pedidoRepository.existsById("P001")).thenReturn(false);
    when(clienteRepository.findById("CLI-123")).thenReturn(Optional.of(new Cliente("CLI-123", "Cliente Test")));
    when(zonaRepository.findById("ZONA1")).thenReturn(Optional.of(new Zona("ZONA1", true)));

    when(pedidoRepository.existsById("P002")).thenReturn(false);
    when(clienteRepository.findById("CLI-999")).thenReturn(Optional.empty());
    when(zonaRepository.findById("ZONA2")).thenReturn(Optional.empty());

    // Ejecutar
    var resultado = useCase.procesarArchivo(file);

    assertEquals(2, resultado.get("totalRegistros"));
    assertEquals(1, resultado.get("guardados"));
    assertTrue(((Map<?, ?>) resultado.get("errores")).containsKey("Clientes no encontrados"));
  }
}
