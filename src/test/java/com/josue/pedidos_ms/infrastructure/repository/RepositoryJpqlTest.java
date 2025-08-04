package com.josue.pedidos_ms.infrastructure.repository;

import com.josue.pedidos_ms.domain.model.Cliente;
import com.josue.pedidos_ms.domain.model.Zona;
import com.josue.pedidos_ms.domain.model.Pedido;
import com.josue.pedidos_ms.domain.model.EstadoPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para validar que las consultas JPQL funcionan correctamente
 */
@DataJpaTest
@ActiveProfiles("test")
class RepositoryJpqlTest {

  @Autowired
  private ClienteRepository clienteRepository;

  @Autowired
  private ZonaRepository zonaRepository;

  @Autowired
  private PedidoRepository pedidoRepository;

  private Cliente cliente1;
  private Cliente cliente2;
  private Zona zona1;
  private Zona zona2;

  @BeforeEach
  void setUp() {
    // Crear datos de prueba
    cliente1 = clienteRepository.save(new Cliente("CLI-001", "Cliente Test 1"));
    cliente2 = clienteRepository.save(new Cliente("CLI-002", "Cliente Test 2"));

    zona1 = zonaRepository.save(new Zona("ZONA-A", true)); // Con refrigeración
    zona2 = zonaRepository.save(new Zona("ZONA-B", false)); // Sin refrigeración

    // Crear pedidos de prueba
    Pedido pedido1 = Pedido.builder()
        .numeroPedido("P001")
        .cliente(cliente1)
        .fechaEntrega(LocalDate.now().plusDays(1))
        .estado(EstadoPedido.PENDIENTE)
        .zonaEntrega(zona1)
        .requiereRefrigeracion(true)
        .build();

    Pedido pedido2 = Pedido.builder()
        .numeroPedido("P002")
        .cliente(cliente2)
        .fechaEntrega(LocalDate.now().plusDays(2))
        .estado(EstadoPedido.CONFIRMADO)
        .zonaEntrega(zona2)
        .requiereRefrigeracion(false)
        .build();

    pedidoRepository.save(pedido1);
    pedidoRepository.save(pedido2);
  }

  // ✅ Tests para ClienteRepository
  @Test
  void debeRetornarTrueCuandoClienteExiste() {
    assertTrue(clienteRepository.existsCliente("CLI-001"));
    assertFalse(clienteRepository.existsCliente("CLI-999"));
  }

  @Test
  void debeBuscarClientePorId() {
    Optional<Cliente> resultado = clienteRepository.buscarCliente("CLI-001");
    assertTrue(resultado.isPresent());
    assertEquals("Cliente Test 1", resultado.get().getNombre());

    Optional<Cliente> noExiste = clienteRepository.buscarCliente("CLI-999");
    assertFalse(noExiste.isPresent());
  }

  @Test
  void debeBuscarTodosLosClientesOrdenados() {
    List<Cliente> clientes = clienteRepository.buscarTodosOrdenados();
    assertEquals(2, clientes.size());
    assertEquals("Cliente Test 1", clientes.get(0).getNombre());
    assertEquals("Cliente Test 2", clientes.get(1).getNombre());
  }

  // ✅ Tests para ZonaRepository
  @Test
  void debeRetornarTrueCuandoZonaExiste() {
    assertTrue(zonaRepository.existsZona("ZONA-A"));
    assertFalse(zonaRepository.existsZona("ZONA-Z"));
  }

  @Test
  void debeBuscarZonaPorId() {
    Optional<Zona> resultado = zonaRepository.buscarZona("ZONA-A");
    assertTrue(resultado.isPresent());
    assertTrue(resultado.get().isSoporteRefrigeracion());

    Optional<Zona> noExiste = zonaRepository.buscarZona("ZONA-Z");
    assertFalse(noExiste.isPresent());
  }

  @Test
  void debeBuscarZonasConRefrigeracion() {
    List<Zona> zonasConRefrig = zonaRepository.buscarZonasConRefrigeracion();
    assertEquals(1, zonasConRefrig.size());
    assertEquals("ZONA-A", zonasConRefrig.get(0).getId());
  }

  @Test
  void debeBuscarZonasPorSoporteRefrigeracion() {
    List<Zona> zonasConRefrig = zonaRepository.buscarZonasPorSoporteRefrigeracion(true);
    assertEquals(1, zonasConRefrig.size());
    assertEquals("ZONA-A", zonasConRefrig.get(0).getId());

    List<Zona> zonasSinRefrig = zonaRepository.buscarZonasPorSoporteRefrigeracion(false);
    assertEquals(1, zonasSinRefrig.size());
    assertEquals("ZONA-B", zonasSinRefrig.get(0).getId());
  }

  // ✅ Tests para PedidoRepository
  @Test
  void debeRetornarTrueCuandoPedidoExiste() {
    assertTrue(pedidoRepository.existePedido("P001"));
    assertFalse(pedidoRepository.existePedido("P999"));
  }

  @Test
  void debeBuscarPedidoPorNumero() {
    Optional<Pedido> resultado = pedidoRepository.buscarPedido("P001");
    assertTrue(resultado.isPresent());
    assertEquals("CLI-001", resultado.get().getCliente().getId());

    Optional<Pedido> noExiste = pedidoRepository.buscarPedido("P999");
    assertFalse(noExiste.isPresent());
  }

  @Test
  void debeBuscarPedidosPorEstadoConDetalles() {
    List<Pedido> pendientes = pedidoRepository.buscarPedidosPorEstadoConDetalles(EstadoPedido.PENDIENTE);
    assertEquals(1, pendientes.size());

    // Verificar que las relaciones están cargadas (JOIN FETCH)
    Pedido pedido = pendientes.get(0);
    assertNotNull(pedido.getCliente());
    assertNotNull(pedido.getZonaEntrega());
    assertEquals("P001", pedido.getNumeroPedido());
  }

  @Test
  void debeBuscarPedidosPorRangoFechas() {
    LocalDate hoy = LocalDate.now();
    LocalDate futuro = hoy.plusDays(3);

    List<Pedido> pedidos = pedidoRepository.buscarPedidosPorRangoFechas(hoy, futuro);
    assertEquals(2, pedidos.size());

    // Verificar ordenamiento por fecha
    assertTrue(pedidos.get(0).getFechaEntrega().isBefore(pedidos.get(1).getFechaEntrega()) ||
        pedidos.get(0).getFechaEntrega().isEqual(pedidos.get(1).getFechaEntrega()));
  }

  @Test
  void debeBuscarPedidosDeClienteConDetalles() {
    List<Pedido> pedidosCliente1 = pedidoRepository.buscarPedidosDeClienteConDetalles("CLI-001");
    assertEquals(1, pedidosCliente1.size());
    assertEquals("P001", pedidosCliente1.get(0).getNumeroPedido());

    // Verificar que las relaciones están cargadas
    assertNotNull(pedidosCliente1.get(0).getCliente());
    assertNotNull(pedidosCliente1.get(0).getZonaEntrega());
  }

  @Test
  void debeContarPedidosConConflictoRefrigeracion() {
    // Crear un pedido que requiere refrigeración pero la zona no la soporta
    Pedido pedidoConflicto = Pedido.builder()
        .numeroPedido("P003")
        .cliente(cliente1)
        .fechaEntrega(LocalDate.now().plusDays(3))
        .estado(EstadoPedido.PENDIENTE)
        .zonaEntrega(zona2) // zona2 no soporta refrigeración
        .requiereRefrigeracion(true) // pero el pedido la requiere
        .build();

    pedidoRepository.save(pedidoConflicto);

    long conflictos = pedidoRepository.contarPedidosConConflictoRefrigeracion();
    assertEquals(1, conflictos);
  }
}
