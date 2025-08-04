package com.josue.pedidos_ms.usecase;

import com.josue.pedidos_ms.application.usecase.CargarPedidosUseCase;
import com.josue.pedidos_ms.domain.model.*;
import com.josue.pedidos_ms.domain.service.*;
import com.josue.pedidos_ms.infrastructure.repository.*;
import com.josue.pedidos_ms.shared.dto.ResultadoCargaResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CargarPedidosUseCaseTest {

  private PedidoRepository pedidoRepository;
  private ClienteRepository clienteRepository;
  private ZonaRepository zonaRepository;
  private PedidoValidator pedidoValidator;
  private ClienteValidator clienteValidator;
  private ZonaValidator zonaValidator;
  private EstadoValidator estadoValidator;
  private FechaValidator fechaValidator;
  private CargarPedidosUseCase useCase;

  @BeforeEach
  void setUp() {
    pedidoRepository = mock(PedidoRepository.class);
    clienteRepository = mock(ClienteRepository.class);
    zonaRepository = mock(ZonaRepository.class);
    pedidoValidator = new PedidoValidator(pedidoRepository);
    clienteValidator = new ClienteValidator(clienteRepository);
    zonaValidator = new ZonaValidator(zonaRepository);
    estadoValidator = new EstadoValidator();
    fechaValidator = new FechaValidator();

    useCase = new CargarPedidosUseCase(
        pedidoRepository,
        clienteRepository,
        zonaRepository,
        pedidoValidator,
        clienteValidator,
        zonaValidator,
        estadoValidator,
        fechaValidator);
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

    // Setup mocks - Usando nuevos métodos JPQL
    when(pedidoRepository.existePedido("P001")).thenReturn(false);
    when(clienteRepository.buscarCliente("CLI-123")).thenReturn(Optional.of(new Cliente("CLI-123", "Cliente Test")));
    when(zonaRepository.buscarZona("ZONA1")).thenReturn(Optional.of(new Zona("ZONA1", true)));

    when(pedidoRepository.existePedido("P002")).thenReturn(false);
    when(clienteRepository.buscarCliente("CLI-999")).thenReturn(Optional.empty());
    when(zonaRepository.buscarZona("ZONA2")).thenReturn(Optional.empty());

    // Ejecutar
    ResultadoCargaResponse resultado = useCase.procesarArchivo(file);

    assertEquals(2, resultado.totalRegistros());
    assertEquals(1, resultado.guardados());
    assertTrue(resultado.errores().containsKey("Clientes no encontrados"));
  }
}
